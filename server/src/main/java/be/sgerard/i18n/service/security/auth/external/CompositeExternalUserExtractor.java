package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.security.auth.external.RawExternalUser;
import be.sgerard.i18n.model.security.user.ExternalUser;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link ExternalUserExtractor external user extractor}.
 *
 * @author Sebastien Gerard
 */
@Primary
@Component
public class CompositeExternalUserExtractor implements ExternalUserExtractor {

    private final List<ExternalUserExtractor> handlers;

    public CompositeExternalUserExtractor(List<ExternalUserExtractor> handlers) {
        this.handlers = handlers;
    }

    @Override
    public boolean support(RawExternalUser externalUser) {
        return handlers.stream()
                .anyMatch(handler -> handler.support(externalUser));
    }

    @Override
    public Mono<ExternalUser> map(RawExternalUser rawExternalUser) {
        return handlers.stream()
                .filter(handler -> handler.support(rawExternalUser))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported user [" + rawExternalUser + "]."))
                .map(rawExternalUser);
    }
}
