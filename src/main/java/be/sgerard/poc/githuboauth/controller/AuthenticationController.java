package be.sgerard.poc.githuboauth.controller;

import be.sgerard.poc.githuboauth.model.auth.UserDto;
import be.sgerard.poc.githuboauth.service.auth.AuthenticationManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Api(value = "Controller handling authentication (current user, or not).")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    public AuthenticationController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/authentication/user")
    @ApiOperation(value = "Retrieves the current authenticated user.")
    public UserDto getCurrentUser() {
        return UserDto.builder(authenticationManager.getCurrentUser()).build();
    }

    @GetMapping("/authentication/authenticated")
    @ApiOperation(value = "Returns whether the current user is authenticated.")
    public boolean isAuthenticated() {
        return authenticationManager.isAuthenticated();
    }
}
