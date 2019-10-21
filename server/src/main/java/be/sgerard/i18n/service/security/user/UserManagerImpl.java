package be.sgerard.i18n.service.security.user;

import be.sgerard.i18n.model.security.user.ExternalUserDto;
import be.sgerard.i18n.model.security.user.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.InternalUserEntity;
import be.sgerard.i18n.model.security.user.UserEntity;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.UUID;

import static java.util.Arrays.asList;

/**
 * @author Sebastien Gerard
 */
@Service
public class UserManagerImpl implements UserManager {

    public static final String DEFAULT_ADMIN_USER = "admin";

    private final UserRepository userRepository;
    private final InternalUserRepository internalUserRepository;
    private final ExternalUserRepository externalUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagerImpl(UserRepository userRepository,
                           InternalUserRepository internalUserRepository,
                           ExternalUserRepository externalUserRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.internalUserRepository = internalUserRepository;
        this.externalUserRepository = externalUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserById(String id) {
        return userRepository.findById(id);
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
    public Optional<InternalUserEntity> getUserByName(String username) {
        return internalUserRepository.findByUsername(username);
    }

    @PostConstruct
    @Transactional
    public void initializeDefaultAdmin(){
        if(!internalUserRepository.findAll().iterator().hasNext()){
            final String password = UUID.randomUUID().toString();

            final InternalUserEntity adminEntity = new InternalUserEntity(DEFAULT_ADMIN_USER);
            adminEntity.setPassword(passwordEncoder.encode(password));
            adminEntity.setRoles(asList(UserRole.MEMBER_OF_ORGANIZATION, UserRole.ADMIN));
            adminEntity.setAvatarUrl("/assets/admin-icon.png");

            System.out.println("====================================================");
            System.out.println("Admin password: " + password);
            System.out.println("====================================================");

            internalUserRepository.save(adminEntity);
        }
    }
}
