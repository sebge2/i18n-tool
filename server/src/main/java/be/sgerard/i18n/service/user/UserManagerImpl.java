package be.sgerard.i18n.service.user;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.user.ExternalUser;
import be.sgerard.i18n.model.user.dto.CurrentUserPasswordUpdateDto;
import be.sgerard.i18n.model.user.dto.CurrentUserPatchDto;
import be.sgerard.i18n.model.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.user.dto.UserPatchDto;
import be.sgerard.i18n.model.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.user.persistence.InternalUserEntity;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.user.ExternalUserRepository;
import be.sgerard.i18n.repository.user.InternalUserRepository;
import be.sgerard.i18n.repository.user.UserRepository;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.user.listener.UserListener;
import be.sgerard.i18n.service.user.validator.UserValidator;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.singleton;

/**
 * Implementation of the {@link UserManager user manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class UserManagerImpl implements UserManager {

    /**
     * Avatar of the default admin user.
     */
    public static final String ADMIN_AVATAR = "/images/admin-icon.png";

    /**
     * Avatar size (height and width).
     */
    public static final int AVATAR_SIZE = 400;

    /**
     * Validation message specifying that the user's password does not match the existing one.
     */
    public static final String VALIDATION_USER_PASSWORD_NOT_MATCH = "validation.user.password-not-match";

    private static final Logger logger = LoggerFactory.getLogger(UserManagerImpl.class);

    private final UserRepository userRepository;
    private final InternalUserRepository internalUserRepository;
    private final ExternalUserRepository externalUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserListener listener;
    private final UserValidator validator;
    private final AppProperties appProperties;

    public UserManagerImpl(UserRepository userRepository,
                           InternalUserRepository internalUserRepository,
                           ExternalUserRepository externalUserRepository,
                           PasswordEncoder passwordEncoder,
                           UserListener listener,
                           UserValidator validator,
                           AppProperties appProperties) {
        this.userRepository = userRepository;
        this.internalUserRepository = internalUserRepository;
        this.externalUserRepository = externalUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.listener = listener;
        this.validator = validator;
        this.appProperties = appProperties;
    }

    @Override
    public Mono<UserEntity> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Flux<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public Mono<InternalUserEntity> createUser(InternalUserCreationDto info) {
        return this
                .initializeUser(info)
                .flatMap(user ->
                        validator
                                .beforePersist(user, info)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return user;
                                })
                )
                .flatMap(externalUserEntity ->
                        validator
                                .beforePersist(externalUserEntity)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return externalUserEntity;
                                })
                )
                .flatMap(internalUserRepository::save)
                .flatMap(user -> listener.afterCreate(user).thenReturn(user));
    }

    @Override
    @Transactional
    public Mono<ExternalUserEntity> createOrUpdate(ExternalUser externalUser) {
        if (!externalUser.isAuthorized()) {
            return Mono.empty();
        }

        return externalUserRepository
                .findByExternalId(externalUser.getExternalId())
                .switchIfEmpty(Mono.defer(() -> Mono.just(new ExternalUserEntity(externalUser.getExternalId(), externalUser.getAuthSystem()))))
                .doOnNext(userEntity -> {
                    userEntity.setRoles(singleton(UserRole.MEMBER_OF_ORGANIZATION));
                    userEntity.setUsername(externalUser.getUsername());
                    userEntity.setDisplayName(externalUser.getDisplayName());
                    userEntity.setAvatarUrl(externalUser.getAvatarUrl());
                    userEntity.setEmail(externalUser.getEmail());
                })
                .flatMap(externalUserEntity ->
                        validator
                                .beforePersist(externalUserEntity, externalUser)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return externalUserEntity;
                                })
                )
                .flatMap(externalUserEntity ->
                        validator
                                .beforePersist(externalUserEntity)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return externalUserEntity;
                                })
                )
                .flatMap(externalUserRepository::save)
                .flatMap(user -> listener.afterCreate(user).thenReturn(user));
    }

    @Override
    @Transactional
    public Mono<UserEntity> update(UserEntity user) {
        return userRepository
                .save(user)
                .flatMap(u -> listener.afterUpdate(u).thenReturn(u));
    }

    @Override
    @Transactional
    public Mono<UserEntity> update(String id, UserPatchDto patch) {
        return findById(id)
                .flatMap(user ->
                        validator
                                .beforeUpdate(user, patch)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return user;
                                })
                )
                .doOnNext(userEntity -> {
                    if (userEntity instanceof InternalUserEntity) {
                        patch.getUsername().ifPresent(((InternalUserEntity) userEntity)::setUsername);
                        patch.getDisplayName().ifPresent(((InternalUserEntity) userEntity)::setDisplayName);
                        patch.getEmail().ifPresent(((InternalUserEntity) userEntity)::setEmail);
                        patch.getPassword().map(passwordEncoder::encode).ifPresent(((InternalUserEntity) userEntity)::setPassword);
                    }

                    patch.getRoles().ifPresent(userEntity::updateAssignableRoles);
                })
                .flatMap(this::update);
    }

    @Override
    @Transactional
    public Mono<UserEntity> updateCurrent(String currentUserId, CurrentUserPatchDto patch) {
        return findByIdOrDie(currentUserId)
                .flatMap(user ->
                        validator
                                .beforeUpdate(user, patch)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return user;
                                })
                )
                .doOnNext(userEntity -> {
                    if (userEntity instanceof InternalUserEntity) {
                        patch.getUsername().ifPresent(((InternalUserEntity) userEntity)::setUsername);
                        patch.getDisplayName().ifPresent(((InternalUserEntity) userEntity)::setDisplayName);
                        patch.getEmail().ifPresent(((InternalUserEntity) userEntity)::setEmail);
                    }
                })
                .flatMap(this::update);
    }

    @Override
    @Transactional
    public Mono<UserEntity> updateUserAvatar(String currentUserId, InputStream avatarStream, String contentType) {
        return findByIdOrDie(currentUserId)
                .flatMap(user ->
                        validator
                                .beforeUpdateAvatar(user)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return user;
                                })
                )
                .flatMap(userEntity ->
                        convertToPngThumbnail(avatarStream)
                                .map(avatar -> {
                                    if (userEntity instanceof InternalUserEntity) {
                                        ((InternalUserEntity) userEntity).setAvatar(avatar);
                                    }

                                    return userEntity;
                                })
                                .flatMap(this::update)
                );
    }

    @Override
    @Transactional
    public Mono<UserEntity> updateCurrentPassword(String currentUserId, CurrentUserPasswordUpdateDto update) {
        return findByIdOrDie(currentUserId)
                .flatMap(user ->
                        validator
                                .beforeUpdatePassword(user, update)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return user;
                                })
                )
                .map(userEntity -> {
                    if (userEntity instanceof InternalUserEntity) {
                        final InternalUserEntity internalUserEntity = (InternalUserEntity) userEntity;

                        checkPassword(internalUserEntity.getPassword(), update.getCurrentPassword());

                        internalUserEntity.setPassword(passwordEncoder.encode(update.getNewPassword()));
                    }

                    return userEntity;
                })
                .flatMap(this::update);
    }

    @Override
    @Transactional
    public Mono<UserEntity> delete(String id) {
        return findById(id)
                .flatMap(user ->
                        validator
                                .beforeDelete(user)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return user;
                                })
                                .flatMap(userEntity ->
                                        userRepository.delete(userEntity)
                                                .thenReturn(userEntity)
                                )
                                .flatMap(rep ->
                                        listener
                                                .afterDelete(rep)
                                                .thenReturn(rep)
                                )
                );
    }

    @Override
    public Mono<InternalUserEntity> finUserByName(String username) {
        return internalUserRepository.findByUsername(username);
    }

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Order(100)
    public Mono<InternalUserEntity> initializeDefaultAdmin() {
        return internalUserRepository
                .findByUsername(ADMIN_USER_NAME)
                .switchIfEmpty(Mono.defer(() -> {
                    final String password = appProperties.getSecurity().getDefaultAdminPassword()
                            .orElseGet(() -> {
                                final String generatedPassword = UUID.randomUUID().toString();

                                System.out.println("====================================================");
                                System.out.println("Admin password: " + generatedPassword);
                                System.out.println("====================================================");

                                return generatedPassword;
                            });

                    return this
                            .createUser(
                                    InternalUserCreationDto.builder()
                                            .username(ADMIN_USER_NAME)
                                            .password(password)
                                            .roles(UserRole.ADMIN)
                                            .displayName(ADMIN_USER_NAME)
                                            .build()
                            )
                            .map(user -> {
                                try {
                                    user.setAvatar(IOUtils.toByteArray(UserManagerImpl.class.getResourceAsStream(ADMIN_AVATAR)));
                                } catch (IOException e) {
                                    logger.error("Error while loading admin avatar.", e);
                                }

                                return user;
                            })
                            .flatMap(internalUserRepository::save);
                }));
    }

    /**
     * Initializes the {@link InternalUserEntity user} based on the initial {@link InternalUserCreationDto creation info}.
     */
    private Mono<InternalUserEntity> initializeUser(InternalUserCreationDto info) {
        final InternalUserEntity user = new InternalUserEntity(info.getUsername(), info.getDisplayName());

        user.setPassword(passwordEncoder.encode(info.getPassword()));

        final Set<UserRole> roles = new HashSet<>(info.getRoles());
        roles.add(UserRole.MEMBER_OF_ORGANIZATION);
        user.setRoles(roles);
        user.setEmail(info.getEmail());

        return Mono.just(user);
    }

    /**
     * Checks that the existing password match.
     */
    private void checkPassword(String currentPassword, String providedPassword) {
        if (!passwordEncoder.matches(providedPassword, currentPassword)) {
            throw new ValidationException(
                    ValidationResult.singleMessage(new ValidationMessage(VALIDATION_USER_PASSWORD_NOT_MATCH))
            );
        }
    }

    /**
     * Converts the specified avatar image to a PNG avatar file.
     */
    private Mono<byte[]> convertToPngThumbnail(InputStream avatar) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Thumbnails.of(avatar)
                    .size(AVATAR_SIZE, AVATAR_SIZE)
                    .outputFormat("png")
                    .toOutputStream(outputStream);

            return Mono.just(outputStream.toByteArray());
        } catch (IOException e) {
            return Mono.error(e);
        }
    }
}
