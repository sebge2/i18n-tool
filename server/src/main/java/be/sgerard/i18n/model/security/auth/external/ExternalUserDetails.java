package be.sgerard.i18n.model.security.auth.external;

import be.sgerard.i18n.model.security.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

import static java.util.Collections.emptyList;

/**
 * Expose the details of an {@link ExternalUserEntity external user}
 *
 * @author Sebastien Gerard
 */
public class ExternalUserDetails implements OAuth2User {

    private final ExternalUserEntity externalUser;
    private final Map<String, Object> attributes;
    private final String token;

    public ExternalUserDetails(ExternalUserEntity externalUser,
                               Map<String, Object> attributes,
                               String token) {
        this.externalUser = externalUser;
        this.attributes = attributes;
        this.token = token;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return UserRole.mapToAuthorities(externalUser.getRoles(), emptyList());
    }

    @Override
    public String getName() {
        return externalUser.getUsername();
    }

    /**
     * Returns the unique id of the associated user.
     */
    public String getId() {
        return externalUser.getId();
    }

    /**
     * Returns the user's roles.
     */
    public Collection<UserRole> getRoles() {
        return externalUser.getRoles();
    }

    /**
     * Returns the current authentication token.
     */
    public String getToken() {
        return token;
    }
}
