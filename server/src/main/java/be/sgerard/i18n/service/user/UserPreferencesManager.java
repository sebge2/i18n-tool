package be.sgerard.i18n.service.user;

import be.sgerard.i18n.model.security.user.dto.UserPreferencesDto;
import be.sgerard.i18n.model.security.user.persistence.UserPreferencesEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;

/**
 * Service handling {@link UserPreferencesEntity user preferences}.
 *
 * @author Sebastien Gerard
 */
public interface UserPreferencesManager {

    /**
     * Returns {@link UserPreferencesManager preferences} of the specified user.
     */
    UserPreferencesEntity getUserPreferences(String userId) throws ResourceNotFoundException;

    /**
     * Updates {@link UserPreferencesManager preferences} for user having the specified id.
     */
    UserPreferencesEntity updateUserPreferences(String userId, UserPreferencesDto preferences) throws ResourceNotFoundException;

}
