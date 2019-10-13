package be.sgerard.i18n.service.security.user;

import be.sgerard.i18n.model.security.user.ExternalUserDto;
import be.sgerard.i18n.model.security.user.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
@Service
public class UserManagerImpl implements UserManager {

    private final UserRepository userRepository;
    private final ExternalUserRepository externalUserRepository;

    public UserManagerImpl(UserRepository userRepository,
                           ExternalUserRepository externalUserRepository) {
        this.userRepository = userRepository;
        this.externalUserRepository = externalUserRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public UserEntity createOrUpdateUser(ExternalUserDto externalUser) {
        final ExternalUserEntity userEntity = externalUserRepository.findByExternalId(externalUser.getExternalId())
                .orElseGet(() -> new ExternalUserEntity(externalUser.getExternalId()));

        userEntity.setUsername(externalUser.getUsername());
        userEntity.setAvatarUrl(externalUser.getAvatarUrl());
        userEntity.setEmail(externalUser.getEmail());

        externalUserRepository.save(userEntity);

        return userEntity;
    }
}
