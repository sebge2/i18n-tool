package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.session.dto.UserLiveSessionDto;
import be.sgerard.i18n.service.security.session.UserLiveSessionManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * {@link RestController Controller} of user live sessions.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Tag(name = "UserLiveSession", description = "Controller of user live sessions.")
public class UserLiveSessionController {

    private final UserLiveSessionManager userSessionManager;

    public UserLiveSessionController(UserLiveSessionManager userSessionManager) {
        this.userSessionManager = userSessionManager;
    }

    /**
     * Returns all the current {@link UserLiveSessionDto user live session}.
     */
    @GetMapping("/user-live-session/")
    @Operation(operationId = "getCurrentLiveSessions", summary = "Retrieves the current user live sessions.")
    public Flux<UserLiveSessionDto> getCurrentLiveSessions() {
        return userSessionManager
                .getCurrentLiveSessions()
                .map(UserLiveSessionDto::toDto);
    }
}
