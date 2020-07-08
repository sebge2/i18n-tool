package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.user.dto.AuthenticatedUserDto;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * Controller handling authentication.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Tag(name = "Authentication", description = "Controller handling authentication (current user, or not).")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final InMemoryReactiveClientRegistrationRepository registrationRepository;

    public AuthenticationController(AuthenticationManager authenticationManager, InMemoryReactiveClientRegistrationRepository registrationRepository) {
        this.authenticationManager = authenticationManager;
        this.registrationRepository = registrationRepository;
    }

    /**
     * Retrieves the current authenticated user.
     */
    @GetMapping("/authentication/user")
    @Operation(summary = "Retrieves the current authenticated user.", security = @SecurityRequirement(name = "basicScheme"))
    public Mono<AuthenticatedUserDto> getCurrentUser() {
        return authenticationManager
                .getCurrentUser()
                .switchIfEmpty(Mono.error(ResourceNotFoundException.userNotFoundException("current")))
                .map(user -> AuthenticatedUserDto.builder(user).build());
    }

    /**
     * Retrieves all the clients that can used to authenticate with OAuth2.
     */
    @GetMapping("/authentication/oauth/client")
    @Operation(summary = "Retrieves all the clients that can used to authenticate with OAuth2.")
    public List<String> getAuthenticationClients() {
        return StreamSupport.stream(registrationRepository.spliterator(), false)
                .map(ClientRegistration::getClientName)
                .sorted()
                .collect(toList());
    }
}
