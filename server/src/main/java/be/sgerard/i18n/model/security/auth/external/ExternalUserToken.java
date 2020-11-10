package be.sgerard.i18n.model.security.auth.external;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * Token associated to a user and coming from an external authentication system.
 *
 * @author Sebastien Gerard
 */
@Value
@SuppressWarnings("RedundantModifiersValueLombok")
public class ExternalUserToken implements Serializable {

    /**
     * The external system that provided the token.
     */
    @EqualsAndHashCode.Exclude
    private final ExternalAuthSystem externalSystem;

    /**
     * The unique token associated to the current authentication.
     */
    private final String token;

}
