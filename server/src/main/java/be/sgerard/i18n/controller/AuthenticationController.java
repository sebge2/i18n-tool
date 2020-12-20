package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.auth.dto.AuthenticatedUserDto;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.security.auth.AuthenticatedUserDtoMapper;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyList;
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

    private final AuthenticationUserManager authenticationUserManager;
    private final AuthenticatedUserDtoMapper userDtoMapper;
    private final Iterable<ClientRegistration> registrationRepository;

    public AuthenticationController(AuthenticationUserManager authenticationUserManager,
                                    AuthenticatedUserDtoMapper userDtoMapper,
                                    @Autowired(required = false) InMemoryReactiveClientRegistrationRepository registrationRepository) {
        this.authenticationUserManager = authenticationUserManager;
        this.userDtoMapper = userDtoMapper;
        this.registrationRepository = (registrationRepository != null) ? registrationRepository : emptyList();
    }

    /**
     * Retrieves the current authenticated user.
     */
    @GetMapping("/authentication/user")
    @Operation(operationId = "getCurrentUser", summary = "Retrieves the current authenticated user.", security = @SecurityRequirement(name = "basicScheme"))
    public Mono<AuthenticatedUserDto> getCurrentUser() {
        return authenticationUserManager
                .getCurrentUser()
                .switchIfEmpty(Mono.error(ResourceNotFoundException.userNotFoundException("current")))
                .flatMap(userDtoMapper::map);
    }

    /**
     * Retrieves all the clients that can used to authenticate with OAuth2.
     */
    @GetMapping("/authentication/oauth/client")
    @Operation(operationId = "getAuthenticationClients", summary = "Retrieves all the clients that can used to authenticate with OAuth2.")
    public List<String> getAuthenticationClients() {
        return StreamSupport.stream(registrationRepository.spliterator(), false)
                .map(ClientRegistration::getClientName)
                .sorted()
                .collect(toList());
    }
}
