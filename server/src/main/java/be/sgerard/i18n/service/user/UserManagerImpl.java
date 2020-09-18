package be.sgerard.i18n.service.user;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.security.user.ExternalUser;
import be.sgerard.i18n.model.security.user.dto.CurrentUserPasswordUpdateDto;
import be.sgerard.i18n.model.security.user.dto.CurrentUserPatchDto;
import be.sgerard.i18n.model.security.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserPatchDto;
import be.sgerard.i18n.model.security.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.persistence.InternalUserEntity;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.user.ExternalUserRepository;
import be.sgerard.i18n.repository.user.InternalUserRepository;
import be.sgerard.i18n.repository.user.UserRepository;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.user.listener.UserListener;
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
    private final AppProperties appProperties;

    public UserManagerImpl(UserRepository userRepository,
                           InternalUserRepository internalUserRepository,
                           ExternalUserRepository externalUserRepository,
                           PasswordEncoder passwordEncoder,
                           UserListener listener,
                           AppProperties appProperties) {
        this.userRepository = userRepository;
        this.internalUserRepository = internalUserRepository;
        this.externalUserRepository = externalUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.listener = listener;
        this.appProperties = appProperties;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserEntity> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public Mono<InternalUserEntity> createUser(InternalUserCreationDto info) {
        return listener
                .beforePersist(info)
                .flatMap(validationResult -> {
                    ValidationException.throwIfFailed(validationResult);

                    final InternalUserEntity user = new InternalUserEntity(info.getUsername(), info.getDisplayName());

                    user.setPassword(passwordEncoder.encode(info.getPassword()));

                    final Set<UserRole> roles = new HashSet<>(info.getRoles());
                    roles.add(UserRole.MEMBER_OF_ORGANIZATION);
                    user.setRoles(roles);
                    user.setEmail(info.getEmail());

                    return internalUserRepository.save(user);
                })
                .flatMap(user ->
                        listener
                                .onCreate(user)
                                .thenReturn(user)
                );
    }

    @Override
    @Transactional
    public Mono<ExternalUserEntity> createOrUpdate(ExternalUser externalUser) {
        if (!externalUser.isAuthorized()) {
            return Mono.empty();
        }

        return externalUserRepository
                .findByExternalId(externalUser.getExternalId())
                .switchIfEmpty(Mono.defer(() -> {
                    final ExternalUserEntity userEntity = new ExternalUserEntity(externalUser.getExternalId(), externalUser.getAuthSystem());

                    userEntity.setRoles(singleton(UserRole.MEMBER_OF_ORGANIZATION));

                    return Mono.just(userEntity);
                }))
                .flatMap(externalUserEntity ->
                        listener
                                .beforePersist(externalUser)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return externalUserEntity;
                                })
                )
                .flatMap(externalUserEntity -> {
                    externalUserEntity.setUsername(externalUser.getUsername());
                    externalUserEntity.setDisplayName(externalUser.getDisplayName());
                    externalUserEntity.setAvatarUrl(externalUser.getAvatarUrl());
                    externalUserEntity.setEmail(externalUser.getEmail());

                    return externalUserRepository.save(externalUserEntity);
                })
                .flatMap(user ->
                        listener
                                .onCreate(user)
                                .thenReturn(user)
                );
    }

    @Override
    @Transactional
    public Mono<UserEntity> update(UserEntity user) {
        return userRepository
                .save(user)
                .flatMap(u ->
                        listener
                                .onUpdate(u)
                                .thenReturn(u)
                );
    }

    @Override
    @Transactional
    public Mono<UserEntity> update(String id, UserPatchDto patch) {
        return findById(id)
                .flatMap(user ->
                        listener
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
                        listener
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
                        listener
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
                        listener
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
                        listener
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
                                                .onDelete(rep)
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
                .findAll()
                .hasElements()
                .flatMap(hasUsers -> {
                    if (hasUsers) {
                        return Mono.empty();
                    }

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
                });
    }

    /**
     * Checks that the existing password match.
     */
    private void checkPassword(String currentPassword, String providedPassword) {
        if (!passwordEncoder.matches(providedPassword, currentPassword)) {
            throw new ValidationException(
                    ValidationResult.builder()
                            .messages(new ValidationMessage(VALIDATION_USER_PASSWORD_NOT_MATCH))
                            .build()
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
