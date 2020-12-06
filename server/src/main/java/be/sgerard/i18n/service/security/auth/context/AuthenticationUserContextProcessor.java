package be.sgerard.i18n.service.security.auth.context;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import reactor.core.publisher.Mono;

import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * Processor of the context associated to every {@link AuthenticatedUser authenticated user}.
 *
 * @author Sebastien Gerard
 */
public interface AuthenticationUserContextProcessor {

    /**
     * Fills the context associated to the newly created {@link AuthenticatedUser user}.
     */
    default Mono<Map<String, Object>> onCreate(AuthenticatedUser authenticatedUser) {
        return Mono.just(emptyMap());
    }
}
