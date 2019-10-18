package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.user.UserDto;
import be.sgerard.i18n.model.security.user.UserEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
import be.sgerard.i18n.service.security.user.UserManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Api(value = "Controller handling authentication (current user, or not).")
public class AuthenticationController {

    private final UserManager userManager;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(UserManager userManager,
                                    AuthenticationManager authenticationManager) {
        this.userManager = userManager;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/authentication/user")
    @ApiOperation(value = "Retrieves the current authenticated user.")
    public UserDto getCurrentUser() {
        final AuthenticatedUser authenticatedUser = authenticationManager.getCurrentAuthenticatedUser()
                .orElseThrow(() -> new ResourceNotFoundException("There is no current user."));

        final UserEntity currentUser = userManager
                .getUserById(authenticatedUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("There is no registered user."));

        return UserDto.builder(currentUser)
                .roles(authenticatedUser.getRoles().stream().map(Enum::name).collect(toList()))
                .build();
    }
}
