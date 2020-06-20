package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.user.dto.UserPreferencesDto;
import be.sgerard.i18n.service.user.UserPreferencesManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    public Mono<UserPreferencesDto> getPreferencesByUserId(@PathVariable String id) {
        return userPreferencesManager.getUserPreferences(id)
                .map(pref -> UserPreferencesDto.builder(pref).build());
    }

    /**
     * Updates preferences for user having the specified id.
     */
    @PutMapping(path = "/user/{id}/preferences")
    @ApiOperation(value = "Updates preferences for user having the specified id.")
    @Transactional
    public Mono<UserPreferencesDto> updateUserPreferences(@PathVariable String id,
                                                          @RequestBody UserPreferencesDto userPreferences) {
        return userPreferencesManager
                .updateUserPreferences(id, userPreferences)
                .map(pref -> UserPreferencesDto.builder(pref).build());
    }
}
