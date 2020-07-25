package be.sgerard.i18n.service.user;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.security.user.ExternalUser;
import be.sgerard.i18n.model.security.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserPatchDto;
import be.sgerard.i18n.model.security.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.persistence.InternalUserEntity;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.repository.user.ExternalUserRepository;
import be.sgerard.i18n.repository.user.InternalUserRepository;
import be.sgerard.i18n.repository.user.UserRepository;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.user.listener.UserListener;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

                    final InternalUserEntity user = new InternalUserEntity(info.getUsername());

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
    public Mono<ExternalUserEntity> createOrUpdate(ExternalUser externalUserDto) {
        return externalUserRepository
                .findByExternalId(externalUserDto.getExternalId())
                .switchIfEmpty(Mono.just(new ExternalUserEntity(externalUserDto.getExternalId(), externalUserDto.getAuthSystem())))
                .flatMap(externalUser ->
                        listener
                                .beforePersist(externalUserDto)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return externalUser;
                                })
                )
                .flatMap(externalUser -> {
                    externalUser.setUsername(externalUserDto.getUsername());
                    externalUser.setAvatarUrl(externalUserDto.getAvatarUrl());
                    externalUser.setEmail(externalUserDto.getEmail());
                    externalUser.setRoles(externalUserDto.getRoles());

                    return externalUserRepository.save(externalUser);
                })
                .flatMap(user ->
                        listener
                                .onCreate(user)
                                .thenReturn(user)
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
                        patch.getEmail().ifPresent(((InternalUserEntity) userEntity)::setEmail);
                        patch.getPassword().ifPresent(((InternalUserEntity) userEntity)::setPassword);
                    }

                    patch.getRoles().ifPresent(userEntity::setRoles);
                })
                .flatMap(this::update);
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

    /**
     * Initializes the default admin user if no user has been defined yet.
     */
    @PostConstruct
    @Transactional
    public void initializeDefaultAdmin() {
        internalUserRepository
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
                })
                .subscribe();
    }
}
