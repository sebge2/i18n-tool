package be.sgerard.i18n.service.user;

import be.sgerard.i18n.model.security.user.dto.UserPreferencesDto;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.model.security.user.persistence.UserPreferencesEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.user.listener.UserPreferencesListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Implementation of the {@link UserPreferencesManager user preferences service}.
 *
 * @author Sebastien Gerard
 */
@Service
public class UserPreferencesManagerImpl implements UserPreferencesManager {

    private final UserManager userManager;
    private final UserPreferencesListener listener;

    public UserPreferencesManagerImpl(UserManager userManager, UserPreferencesListener listener) {
        this.userManager = userManager;
        this.listener = listener;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserPreferencesEntity> getUserPreferences(String userId) throws ResourceNotFoundException {
        return findUserOrDie(userId)
                .map(UserEntity::getPreferences);
    }

    @Override
    public Mono<UserPreferencesEntity> updateUserPreferences(String userId, UserPreferencesDto preferences) throws ResourceNotFoundException {
        return getUserPreferences(userId)
                .doOnNext(pref -> {
                    pref.setToolLocale(preferences.getToolLocale().orElse(null));
                })
                .flatMap(pref ->
                        listener
                                .onUpdate(pref)
                                .thenReturn(pref)
                );

    }

    /**
     * Returns the {@link UserEntity user} having the specified id.
     */
    private Mono<UserEntity> findUserOrDie(String userId) {
        return userManager.findByIdOrDie(userId);
    }
}
