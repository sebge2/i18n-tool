package be.sgerard.i18n.service.security.user;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.user.*;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static java.util.Arrays.asList;

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

    public UserManagerImpl(UserRepository userRepository,
                           InternalUserRepository internalUserRepository,
                           ExternalUserRepository externalUserRepository,
                           PasswordEncoder passwordEncoder,
                           EventService eventService) {
        this.userRepository = userRepository;
        this.internalUserRepository = internalUserRepository;
        this.externalUserRepository = externalUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventService = eventService;
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
    public ExternalUserEntity createOrUpdateUser(ExternalUserDto externalUser) {
        final ExternalUserEntity userEntity = externalUserRepository.findByExternalId(externalUser.getExternalId())
                .orElseGet(() -> new ExternalUserEntity(externalUser.getExternalId()));

        userEntity.setUsername(externalUser.getUsername());
        userEntity.setAvatarUrl(externalUser.getAvatarUrl());
        userEntity.setEmail(externalUser.getEmail());

        externalUserRepository.save(userEntity);

        return userEntity;
    }

    @Override
    @Transactional
    public UserEntity updateUser(String id, UserUpdateDto userUpdate) {
        final UserEntity userEntity = getUserById(id).orElseThrow(() -> new ResourceNotFoundException("There is no user [" + id + "]."));

        validate(userUpdate, userEntity);

        if (userEntity instanceof InternalUserEntity) {
            userUpdate.getUsername().ifPresent(((InternalUserEntity) userEntity)::setUsername);
            userUpdate.getEmail().ifPresent(((InternalUserEntity) userEntity)::setEmail);
            userUpdate.getAvatarUrl().ifPresent(((InternalUserEntity) userEntity)::setAvatarUrl);
            userUpdate.getPassword().ifPresent(((InternalUserEntity) userEntity)::setPassword);
        } else if (userEntity instanceof ExternalUserEntity) {

        } else {
            throw new UnsupportedOperationException("Unsupported user [" + userEntity + "].");
        }

        userUpdate.getRoles().ifPresent(userEntity::setRoles);

        final UserDto updatedUserDto = UserDto.builder(userEntity).build();

        eventService.broadcastInternally(EventType.EVENT_UPDATED_USER, updatedUserDto);
        eventService.sendEventToUser(UserRole.ADMIN, EventType.EVENT_UPDATED_USER, updatedUserDto);
        eventService.sendEventToUser(userEntity, EventType.EVENT_UPDATED_CURRENT_USER, updatedUserDto);

        return userEntity;
    }

    @Override
    @Transactional
    public void deleteUserById(String id) {
        userRepository.deleteById(id);
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

            final InternalUserEntity adminEntity = new InternalUserEntity(DEFAULT_ADMIN_USER);
            adminEntity.setPassword(passwordEncoder.encode(password));
            adminEntity.setRoles(asList(UserRole.MEMBER_OF_ORGANIZATION, UserRole.ADMIN));
            adminEntity.setAvatarUrl(ADMIN_AVATAR);

            System.out.println("====================================================");
            System.out.println("Admin password: " + password);
            System.out.println("====================================================");

            internalUserRepository.save(adminEntity);
        }
    }

    private void validate(UserUpdateDto userUpdate, UserEntity userEntity) {
        final ValidationResult.Builder builder = ValidationResult.builder();

        userUpdate.getRoles()
                .ifPresent(roles -> roles.stream()
                        .filter(role -> !role.isAssignableByEndUser())
                        .forEach(role -> builder.messages(new ValidationMessage("ROLE_UN_ASSIGNABLE", role.name())))
                );

        if (userEntity instanceof ExternalUserEntity) {
// TODO
        } else if (userEntity instanceof InternalUserEntity) {
// TODO
        } else {
            throw new UnsupportedOperationException("Unsupported user [" + userEntity + "].");
        }

        ValidationException.throwIfFailed(builder.build());
    }
}
