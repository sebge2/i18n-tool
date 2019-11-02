package be.sgerard.i18n.service.security.user;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.user.*;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.user.validator.UserValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Sebastien Gerard
 */
@Service
public class UserManagerImpl implements UserManager {

    public static final String DEFAULT_ADMIN_USER = "admin";
    public static final String ADMIN_AVATAR = "/assets/admin-icon.png";

    private final UserRepository userRepository;
    private final InternalUserRepository internalUserRepository;
    private final ExternalUserRepository externalUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventService eventService;
    private final List<UserValidator> validators;

    public UserManagerImpl(UserRepository userRepository,
                           InternalUserRepository internalUserRepository,
                           ExternalUserRepository externalUserRepository,
                           PasswordEncoder passwordEncoder,
                           EventService eventService,
                           List<UserValidator> validators) {
        this.userRepository = userRepository;
        this.internalUserRepository = internalUserRepository;
        this.externalUserRepository = externalUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventService = eventService;
        this.validators = validators;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<UserEntity> loadAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public InternalUserEntity createUser(UserCreationDto info) {
        ValidationException.throwIfFailed(
                validators.stream()
                        .map(validator -> validator.validateOnCreate(info))
                        .collect(ValidationResult.toValidationResult())
        );

        final InternalUserEntity userEntity = new InternalUserEntity(info.getUsername());

        userEntity.setPassword(passwordEncoder.encode(info.getPassword()));
        userEntity.setRoles(info.getRoles());
        userEntity.setAvatarUrl(info.getAvatarUrl());
        userEntity.setEmail(info.getEmail());

        internalUserRepository.save(userEntity);

        eventService.broadcastInternally(EventType.UPDATED_USER, UserDto.builder(userEntity).build());
        eventService.sendEventToUser(UserRole.ADMIN, EventType.UPDATED_USER, UserDto.builder(userEntity).build());

        return userEntity;
    }

    @Override
    @Transactional
    public ExternalUserEntity createOrUpdateUser(ExternalUserDto externalUser) {
        final ExternalUserEntity userEntity = externalUserRepository.findByExternalId(externalUser.getExternalId())
                .orElseGet(() -> new ExternalUserEntity(externalUser.getExternalId()));

        userEntity.setUsername(externalUser.getUsername());
        userEntity.setAvatarUrl(externalUser.getAvatarUrl());
        userEntity.setEmail(externalUser.getEmail());

        externalUserRepository.save(userEntity);

        final UserDto updatedUserDto = UserDto.builder(userEntity).build();

        eventService.broadcastInternally(EventType.UPDATED_USER, updatedUserDto);
        eventService.sendEventToUser(UserRole.ADMIN, EventType.UPDATED_USER, updatedUserDto);
        eventService.sendEventToUser(updatedUserDto, EventType.UPDATED_CURRENT_USER, updatedUserDto);

        return userEntity;
    }

    @Override
    @Transactional
    public UserEntity updateUser(String id, UserUpdateDto userUpdate) {
        final UserEntity userEntity = getUserById(id).orElseThrow(() -> new ResourceNotFoundException("There is no user [" + id + "]."));

        ValidationException.throwIfFailed(
                validators.stream()
                        .map(validator -> validator.validateOnUpdate(userUpdate, userEntity))
                        .collect(ValidationResult.toValidationResult())
        );

        if (userEntity instanceof InternalUserEntity) {
            userUpdate.getUsername().ifPresent(((InternalUserEntity) userEntity)::setUsername);
            userUpdate.getEmail().ifPresent(((InternalUserEntity) userEntity)::setEmail);
            userUpdate.getAvatarUrl().ifPresent(((InternalUserEntity) userEntity)::setAvatarUrl);
            userUpdate.getPassword().ifPresent(((InternalUserEntity) userEntity)::setPassword);
        }

        userUpdate.getRoles().ifPresent(userEntity::setRoles);

        final UserDto updatedUserDto = UserDto.builder(userEntity).build();

        eventService.broadcastInternally(EventType.UPDATED_USER, updatedUserDto);
        eventService.sendEventToUser(UserRole.ADMIN, EventType.UPDATED_USER, updatedUserDto);
        eventService.sendEventToUser(updatedUserDto, EventType.UPDATED_CURRENT_USER, updatedUserDto);

        return userEntity;
    }

    @Override
    @Transactional
    public void deleteUserById(String id) {
        getUserById(id).ifPresent(
                userEntity -> {
                    ValidationException.throwIfFailed(
                            validators.stream()
                                    .map(validator -> validator.validateOnDelete(userEntity))
                                    .collect(ValidationResult.toValidationResult())
                    );

                    userRepository.delete(userEntity);
                    eventService.broadcastInternally(EventType.DELETED_USER, UserDto.builder(userEntity).build());
                    eventService.sendEventToUser(UserRole.ADMIN, EventType.DELETED_USER, UserDto.builder(userEntity).build());
                }
        );
    }

    @Override
    public Optional<InternalUserEntity> getUserByName(String username) {
        return internalUserRepository.findByUsername(username);
    }

    @PostConstruct
    @Transactional
    public void initializeDefaultAdmin() {
        if (!internalUserRepository.findAll().iterator().hasNext()) {
            final String password = UUID.randomUUID().toString();

            createUser(
                    UserCreationDto.builder()
                            .username(DEFAULT_ADMIN_USER)
                            .password(password)
                            .roles(UserRole.MEMBER_OF_ORGANIZATION, UserRole.ADMIN)
                            .avatarUrl(ADMIN_AVATAR)
                            .build()
            );

            System.out.println("====================================================");
            System.out.println("Admin password: " + password);
            System.out.println("====================================================");
        }
    }
}
