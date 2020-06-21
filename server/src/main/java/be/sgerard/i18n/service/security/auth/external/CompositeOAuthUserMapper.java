package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import be.sgerard.i18n.model.security.user.dto.ExternalUserDto;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link OAuthUserMapper OAuth external user handler}.
 *
 * @author Sebastien Gerard
 */
@Primary
@Component
public class CompositeOAuthUserMapper implements OAuthUserMapper {

    private final List<OAuthUserMapper> handlers;

    public CompositeOAuthUserMapper(List<OAuthUserMapper> handlers) {
        this.handlers = handlers;
    }

    @Override
    public boolean support(OAuthExternalUser externalUser) {
        return handlers.stream()
                .anyMatch(handler -> handler.support(externalUser));
    }

    @Override
    public Mono<ExternalUserDto> map(OAuthExternalUser externalUser) {
        return handlers.stream()
                .filter(handler -> handler.support(externalUser))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported user [" + externalUser + "]."))
                .map(externalUser);
    }
}
