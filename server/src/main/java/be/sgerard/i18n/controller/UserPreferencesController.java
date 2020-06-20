package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.user.dto.UserPreferencesDto;
import be.sgerard.i18n.service.user.UserPreferencesManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user preferences.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Api(value = "Controller of user preferences.")
public class UserPreferencesController {

    private final UserPreferencesManager userPreferencesManager;

    public UserPreferencesController(UserPreferencesManager userPreferencesManager) {
        this.userPreferencesManager = userPreferencesManager;
    }

    /**
     * Returns preferences for user having the specified id.
     */
    @GetMapping(path = "/user/{id}/preferences")
    @ApiOperation(value = "Returns preferences for user having the specified id.")
    @Transactional(readOnly = true)
    public UserPreferencesDto getPreferencesByUserId(@PathVariable String id) {
        return UserPreferencesDto.builder(userPreferencesManager.getUserPreferences(id)).build();
    }

    /**
     * Updates preferences for user having the specified id.
     */
    @PutMapping(path = "/user/{id}/preferences")
    @ApiOperation(value = "Updates preferences for user having the specified id.")
    @Transactional
    public UserPreferencesDto updateUserPreferences(@PathVariable String id,
                                                    @RequestBody UserPreferencesDto userPreferences) {
        return UserPreferencesDto.builder(userPreferencesManager.updateUserPreferences(id, userPreferences)).build();
    }
}
