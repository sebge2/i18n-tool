package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.user.dto.UserPreferencesDto;
import be.sgerard.i18n.service.user.UserPreferencesManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Controller for user preferences.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Tag(name = "User Preferences", description = "Controller of user preferences.")
public class UserPreferencesController {

    private final UserPreferencesManager userPreferencesManager;

    public UserPreferencesController(UserPreferencesManager userPreferencesManager) {
        this.userPreferencesManager = userPreferencesManager;
    }

    /**
     * Returns preferences for the current user.
     */
    @GetMapping(path = "/user/current/preferences")
    @Operation(operationId = "getCurrentUserPreferences", summary = "Returns preferences for the current user.")
    public Mono<UserPreferencesDto> getCurrentUserPreferences() {
        return userPreferencesManager.get()
                .map(pref -> UserPreferencesDto.builder(pref).build());
    }

    /**
     * Updates preferences for the current user.
     */
    @PutMapping(path = "/user/current/preferences")
    @Operation(operationId = "updateCurrentUserPreferences", summary = "Updates preferences for the current user.")
    public Mono<UserPreferencesDto> updateCurrentUserPreferences(@RequestBody UserPreferencesDto userPreferences) {
        return userPreferencesManager
                .update(userPreferences)
                .map(pref -> UserPreferencesDto.builder(pref).build());
    }
}
