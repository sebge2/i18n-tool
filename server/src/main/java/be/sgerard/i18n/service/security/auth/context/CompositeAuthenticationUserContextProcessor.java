package be.sgerard.i18n.service.security.auth.context;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Composite {@link AuthenticationUserContextProcessor processor}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeAuthenticationUserContextProcessor implements AuthenticationUserContextProcessor {

    private final List<AuthenticationUserContextProcessor> processors;

    public CompositeAuthenticationUserContextProcessor(List<AuthenticationUserContextProcessor> processors) {
        this.processors = processors;
    }

    @Override
    public Mono<Map<String, Object>> onCreate(AuthenticatedUser authenticatedUser) {
        return Flux
                .fromIterable(processors)
                .flatMap(processor -> processor.onCreate(authenticatedUser))
                .reduce((first, second) -> {
                    final Map<String, Object> merged = new HashMap<>();

                    merged.putAll(first);
                    merged.putAll(second);

                    return merged;
                })
                .switchIfEmpty(Mono.just(new HashMap<>()));
    }
}
