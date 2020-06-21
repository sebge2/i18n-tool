package be.sgerard.i18n.model.security.auth;

import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

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

    private final String id;
    private final UserDto user;
    private final String token;
    private final Set<UserRole> roles;
    private final Map<String, RepositoryCredentials> repositoryCredentials;

    public ExternalAuthenticatedUser(String id,
                                     UserDto user,
                                     String token,
                                     Collection<UserRole> roles,
                                     Collection<RepositoryCredentials> repositoryCredentials) {
        super(mapToAuthorities(roles), singletonMap(NAME_ATTRIBUTE, user.getId()), NAME_ATTRIBUTE);

        this.id = id;
        this.user = user;
        this.token = token;
        this.roles = Set.copyOf(roles);
        this.repositoryCredentials = repositoryCredentials.stream().collect(toMap(RepositoryCredentials::getRepository, auth -> auth));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public UserDto getUser() {
        return user;
    }

    @Override
    public String getName() {
        return getUser().getId();
    }

    @Override
    public Collection<UserRole> getSessionRoles() {
        return roles;
    }

    @Override
    public <A extends RepositoryCredentials> Optional<A> getCredentials(String repository, Class<A> expectedType) {
        return Optional.ofNullable(repositoryCredentials.get(repository))
                .map(expectedType::cast);
    }

    @Override
    public ExternalAuthenticatedUser updateSessionRoles(List<UserRole> sessionRoles) {
        return new ExternalAuthenticatedUser(
                id,
                user,
                token,
                sessionRoles,
                repositoryCredentials.values()
        );
    }

    /**
     * Returns the token associated to this authentication.
     */
    public String getToken() {
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
        if (!super.equals(o)) return false;

        final ExternalAuthenticatedUser that = (ExternalAuthenticatedUser) o;

        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user);
    }

    /**
     * Maps the specified roles to authorities.
     */
    private static Set<GrantedAuthority> mapToAuthorities(Collection<UserRole> roles) {
        return Stream
                .concat(
                        Stream.of(ROLE_USER),
                        roles.stream().map(UserRole::toAuthority)
                )
                .collect(toSet());
    }
}
