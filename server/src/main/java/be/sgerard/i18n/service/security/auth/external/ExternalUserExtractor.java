package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.security.auth.external.RawExternalUser;
import be.sgerard.i18n.model.user.ExternalUser;
import reactor.core.publisher.Mono;

/**
 * Extractor of information coming from an external system to an internal representation of this user.
 *
 * @author Sebastien Gerard
 */
public interface ExternalUserExtractor {

    /**
     * Returns whether the specified {@link RawExternalUser user} is supported.
     */
    boolean support(RawExternalUser externalUser);

    /**
     * Extracts from the specified {@link RawExternalUser user} an {@link ExternalUser external user}.
     */
    Mono<ExternalUser> map(RawExternalUser externalUser);
}
