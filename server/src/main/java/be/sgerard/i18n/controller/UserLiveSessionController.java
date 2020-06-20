package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.session.UserLiveSessionDto;
import be.sgerard.i18n.service.security.session.UserLiveSessionManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "Controller of user live sessions.")
public class UserLiveSessionController {

    private final UserLiveSessionManager userSessionManager;

    public UserLiveSessionController(UserLiveSessionManager userSessionManager) {
        this.userSessionManager = userSessionManager;
    }

    /**
     * Returns all the current {@link UserLiveSessionDto user live session}.
     */
    @GetMapping("/user-live-session/")
    @ApiOperation(value = "Retrieves the current user live sessions.")
    public Flux<UserLiveSessionDto> getCurrentLiveSessions() {
        return userSessionManager
                .getCurrentLiveSessions()
                .map(session -> UserLiveSessionDto.builder(session).build());
    }
}
