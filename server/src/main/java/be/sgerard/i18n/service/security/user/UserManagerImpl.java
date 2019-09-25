package be.sgerard.i18n.service.security.user;

import be.sgerard.i18n.model.security.user.ExternalUserDto;
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

    public UserManagerImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public UserEntity createOrUpdateUser(ExternalUserDto externalUser) {
        final UserEntity userEntity = userRepository.findByExternalId(externalUser.getExternalId())
                .orElseGet(() -> new UserEntity(externalUser.getExternalId()));

        userEntity.setUsername(externalUser.getUsername());
        userEntity.setAvatarUrl(externalUser.getAvatarUrl());
        userEntity.setEmail(externalUser.getEmail());

        userRepository.save(userEntity);

        return userEntity;
    }
}
