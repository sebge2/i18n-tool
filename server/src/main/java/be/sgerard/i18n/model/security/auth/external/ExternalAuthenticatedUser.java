package be.sgerard.i18n.model.security.auth.external;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.*;

import static be.sgerard.i18n.service.security.UserRole.mapToAuthorities;
import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableMap;

/**
 * {@link AuthenticatedUser Authenticated external user}.
 *
 * @author Sebastien Gerard
 */
public final class ExternalAuthenticatedUser extends DefaultOAuth2User implements AuthenticatedUser {

    /**
     * Name of the attribute containing the unique user id.
     */
    public static final String NAME_ATTRIBUTE = "principal_id";

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String userId;
    private final ExternalUserToken token;
    private final Set<UserRole> roles;
    private final String displayName;
    private final String email;
    private final Collection<GrantedAuthority> additionalAuthorities;
    private final Map<String, Object> context;

    public ExternalAuthenticatedUser(String id,
                                     String userId,
                                     ExternalUserToken token,
                                     Collection<UserRole> roles,
                                     String displayName,
                                     String email,
                                     Collection<GrantedAuthority> additionalAuthorities,
                                     Map<String, Object> context) {
        super(mapToAuthorities(roles, additionalAuthorities), singletonMap(NAME_ATTRIBUTE, userId), NAME_ATTRIBUTE);

        this.id = id;
        this.userId = userId;
        this.token = token;
        this.roles = Set.copyOf(roles);
        this.displayName = displayName;
        this.email = email;
        this.additionalAuthorities = additionalAuthorities;
        this.context = new HashMap<>(context);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public Collection<UserRole> getRoles() {
        return roles;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public ExternalAuthenticatedUser updateRoles(List<UserRole> sessionRoles) {
        return new ExternalAuthenticatedUser(
                id,
                userId,
                token,
                sessionRoles,
                displayName,
                email,
                additionalAuthorities,
                context
        );
    }

    @Override
    public Map<String, Object> getContext() {
        return unmodifiableMap(context);
    }

    @Override
    public <V> Optional<V> getContextValue(String key, Class<V> valueType) {
        return Optional
                .ofNullable(context.get(key))
                .map(valueType::cast);
    }

    @Override
    public AuthenticatedUser updateContext(String key, Object value) {
        final Map<String, Object> updatedContext = new HashMap<>(context);
        updatedContext.put(key, value);

        return new ExternalAuthenticatedUser(
                id,
                userId,
                token,
                roles,
                displayName,
                email,
                additionalAuthorities,
                updatedContext
        );
    }

    /**
     * Returns the {@link ExternalUserToken token} associated to this authentication.
     */
    public ExternalUserToken getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ExternalAuthenticatedUser that = (ExternalAuthenticatedUser) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
