package be.sgerard.i18n.service.user;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.security.user.dto.ExternalUserDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Optional;
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
    public static final String ADMIN_AVATAR = "/assets/admin-icon.png";

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
        return Mono.justOrEmpty(userRepository.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<UserEntity> findAll() {
        return Flux.fromIterable(userRepository.findAll());
    }

    @Override
    @Transactional
    public Mono<InternalUserEntity> createUser(InternalUserCreationDto info) {
        return listener
                .beforePersist(info)
                .map(validationResult -> {
                    ValidationException.throwIfFailed(validationResult);

                    final InternalUserEntity user = new InternalUserEntity(info.getUsername());

                    user.setPassword(passwordEncoder.encode(info.getPassword()));

                    final Set<UserRole> roles = new HashSet<>(info.getRoles());
                    roles.add(UserRole.MEMBER_OF_ORGANIZATION);
                    user.setRoles(roles);

                    user.setAvatarUrl(info.getAvatarUrl());
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
    public Mono<ExternalUserEntity> createOrUpdateUser(ExternalUserDto externalUserDto) {
        return Mono
                .justOrEmpty(externalUserRepository.findByExternalId(externalUserDto.getExternalId()))
                .switchIfEmpty(Mono.just(new ExternalUserEntity(externalUserDto.getExternalId())))
                .flatMap(externalUser ->
                        listener
                                .beforePersist(externalUserDto)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return externalUser;
                                })
                )
                .map(externalUser -> {
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
    public Mono<UserEntity> updateUser(String id, UserPatchDto patch) {
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
                        patch.getAvatarUrl().ifPresent(((InternalUserEntity) userEntity)::setAvatarUrl);
                        patch.getPassword().ifPresent(((InternalUserEntity) userEntity)::setPassword);
                    }

                    patch.getRoles().ifPresent(userEntity::setRoles);
                })
                .flatMap(user ->
                        listener
                                .onUpdate(user)
                                .thenReturn(user)
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
                                .map(userEntity -> {
                                    userRepository.delete(userEntity);
                                    return userEntity;
                                })
                                .flatMap(rep ->
                                        listener
                                                .onDelete(rep)
                                                .thenReturn(rep)
                                )
                );
    }

    @Override
    public Mono<InternalUserEntity> finUserByName(String username) {
        return Mono.justOrEmpty(internalUserRepository.findByUsername(username));
    }

    /**
     * Initializes the default admin user if no user has been defined yet.
     */
    @PostConstruct
    @Transactional
    public Optional<InternalUserEntity> initializeDefaultAdmin() {
        return Flux
                .fromIterable(internalUserRepository.findAll())
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

                    return createUser(
                            InternalUserCreationDto.builder()
                                    .username(ADMIN_USER_NAME)
                                    .password(password)
                                    .roles(UserRole.ADMIN)
                                    .avatarUrl(ADMIN_AVATAR)
                                    .build()
                    );
                })
                .blockOptional();
    }
}
