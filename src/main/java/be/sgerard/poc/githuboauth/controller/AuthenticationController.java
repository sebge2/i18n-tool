package be.sgerard.poc.githuboauth.controller;

import be.sgerard.poc.githuboauth.model.auth.UserDto;
import be.sgerard.poc.githuboauth.service.auth.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sebastien Gerard
 */
@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    public AuthenticationController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/authentication/user")
    public UserDto getCurrentUser() {
        return authenticationManager.getCurrentUser();
    }

    @GetMapping("/authentication/authenticated")
    public boolean isAuthenticated() {
        return authenticationManager.isAuthenticated();
    }
}
