package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import be.sgerard.i18n.model.security.user.ExternalUser;
import reactor.core.publisher.Mono;

/**
 * Mapper extracting information about an OAuth user to an internal representation of it.
 *
 * @author Sebastien Gerard
 */
public interface OAuthUserMapper {

    /**
     * Returns whether the specified {@link OAuthExternalUser user} is supported.
     */
    boolean support(OAuthExternalUser externalUser);

    /**
     * Maps the specified {@link OAuthExternalUser user} to an {@link ExternalUser external user}.
     */
    Mono<ExternalUser> map(OAuthExternalUser externalUser);
}
