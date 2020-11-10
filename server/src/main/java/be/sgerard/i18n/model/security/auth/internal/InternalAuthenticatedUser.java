package be.sgerard.i18n.model.security.auth.internal;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

import static be.sgerard.i18n.service.security.UserRole.mapToAuthorities;
import static java.util.Collections.unmodifiableMap;

/**
 * {@link AuthenticatedUser Authenticated internal user}.
 *
 * @author Sebastien Gerard
 */
public class InternalAuthenticatedUser implements AuthenticatedUser {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String userId;
    private final Set<UserRole> roles;
    private final String displayName;
    private final String email;
    private final Collection<GrantedAuthority> additionalAuthorities;
    private final Map<String, Object> context;

    public InternalAuthenticatedUser(String id,
                                     String userId,
                                     Collection<UserRole> roles,
                                     String displayName,
                                     String email,
                                     Collection<GrantedAuthority> additionalAuthorities,
                                     Map<String, Object> context) {
        this.id = id;
        this.userId = userId;
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
    public String getName() {
        return getUserId();
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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return mapToAuthorities(roles, additionalAuthorities);
    }

    @Override
    public InternalAuthenticatedUser updateRoles(List<UserRole> sessionRoles) {
        return new InternalAuthenticatedUser(
                id,
                userId,
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

        return new InternalAuthenticatedUser(
                id,
                userId,
                roles,
                displayName,
                email,
                additionalAuthorities,
                updatedContext
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) return false;

        final InternalAuthenticatedUser that = (InternalAuthenticatedUser) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
