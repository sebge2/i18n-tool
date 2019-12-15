package be.sgerard.i18n.service.user;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.user.UserDto;
import be.sgerard.i18n.model.security.user.UserEntity;
import be.sgerard.i18n.model.security.user.UserPreferencesDto;
import be.sgerard.i18n.model.security.user.UserPreferencesEntity;
import be.sgerard.i18n.repository.user.UserRepository;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.event.EventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link UserPreferencesManager user preferences service}.
 *
 * @author Sebastien Gerard
 */
@Service
public class UserPreferencesManagerImpl implements UserPreferencesManager {

    private final UserRepository userRepository;
    private final EventService eventService;

    public UserPreferencesManagerImpl(UserRepository userRepository, EventService eventService) {
        this.userRepository = userRepository;
        this.eventService = eventService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserPreferencesEntity getUserPreferences(String userId) throws ResourceNotFoundException {
        return findUserOrDie(userId).getPreferences();
    }

    @Override
    public UserPreferencesEntity updateUserPreferences(String userId, UserPreferencesDto preferences) throws ResourceNotFoundException {
        final UserPreferencesEntity existingPreferences = findUserOrDie(userId).getPreferences();

        existingPreferences.setToolLocale(preferences.getToolLocale().orElse(null));

        eventService.sendEventToUser(UserDto.builder(existingPreferences.getUser()).build(), EventType.UPDATED_USER_PREFERENCES ,preferences);

        return existingPreferences;
    }

    /**
     * Returns the {@link UserEntity user} having the specified id.
     */
    private UserEntity findUserOrDie(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.userNotFoundException(userId));
    }
}
