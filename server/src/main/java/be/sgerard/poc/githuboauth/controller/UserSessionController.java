package be.sgerard.poc.githuboauth.controller;

import be.sgerard.poc.githuboauth.model.security.session.UserSessionDto;
import be.sgerard.poc.githuboauth.service.security.session.UserSessionManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Api(value = "Controller user sessions.")
public class UserSessionController {

    private final UserSessionManager userSessionManager;

    public UserSessionController(UserSessionManager userSessionManager) {
        this.userSessionManager = userSessionManager;
    }

    @GetMapping("/user-session/current")
    @ApiOperation(value = "Retrieves currently authenticated user.")
    public Collection<UserSessionDto> getCurrentUser() {
        return userSessionManager.getCurrentSessions().stream().map(entity -> UserSessionDto.userSessionDto(entity).build()).collect(toList());
    }
}
