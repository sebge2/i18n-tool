package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.user.AuthenticatedUserDto;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
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
    public AuthenticatedUserDto getCurrentUser() {
        final AuthenticatedUser authenticatedUser = authenticationManager.getCurrentUser()
                .orElseThrow(() -> new ResourceNotFoundException("There is no current user."));

        return AuthenticatedUserDto.builder(authenticatedUser).build();
    }
}
