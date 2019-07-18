package be.sgerard.poc.githuboauth.controller;

import be.sgerard.poc.githuboauth.model.security.user.UserDto;
import be.sgerard.poc.githuboauth.model.security.user.UserEntity;
import be.sgerard.poc.githuboauth.service.ResourceNotFoundException;
import be.sgerard.poc.githuboauth.service.security.auth.AuthenticationManager;
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
        final UserEntity currentUser = authenticationManager.getCurrentUser()
                .orElseThrow(() -> new ResourceNotFoundException("There is no current user."));

        return UserDto.builder(currentUser)
                .roles(authenticationManager.getCurrentUserRoles())
                .build();
    }
}
