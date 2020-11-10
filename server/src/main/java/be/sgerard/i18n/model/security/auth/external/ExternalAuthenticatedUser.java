package be.sgerard.i18n.model.security.auth.external;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.*;

import static be.sgerard.i18n.service.security.UserRole.mapToAuthorities;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
    private final Collection<GrantedAuthority> additionalAuthorities;
    private final Map<String, RepositoryCredentials> repositoryCredentials;

    public ExternalAuthenticatedUser(String id,
                                     String userId,
                                     ExternalUserToken token,
                                     Collection<UserRole> roles,
                                     Collection<GrantedAuthority> additionalAuthorities,
                                     Collection<RepositoryCredentials> repositoryCredentials) {
        super(mapToAuthorities(roles, additionalAuthorities), singletonMap(NAME_ATTRIBUTE, userId), NAME_ATTRIBUTE);

        this.id = id;
        this.userId = userId;
        this.token = token;
        this.roles = Set.copyOf(roles);
        this.additionalAuthorities = additionalAuthorities;
        this.repositoryCredentials = repositoryCredentials.stream().collect(toMap(RepositoryCredentials::getRepository, auth -> auth));
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
    public <A extends RepositoryCredentials> Optional<A> getCredentials(String repository, Class<A> expectedType) {
        return Optional.ofNullable(repositoryCredentials.get(repository))
                .map(expectedType::cast);
    }

    @Override
    public Collection<RepositoryCredentials> getRepositoryCredentials() {
        return repositoryCredentials.values();
    }

    @Override
    public ExternalAuthenticatedUser updateRoles(List<UserRole> sessionRoles) {
        return new ExternalAuthenticatedUser(
                id,
                userId,
                token,
                sessionRoles,
                additionalAuthorities,
                repositoryCredentials.values()
        );
    }

    @Override
    public AuthenticatedUser updateRepositoryCredentials(RepositoryCredentials repositoryCredentials) {
        final Set<RepositoryCredentials> repositoriesCredentials = new HashSet<>(this.repositoryCredentials.values());
        repositoriesCredentials.add(repositoryCredentials);

        return new ExternalAuthenticatedUser(
                id,
                userId,
                token,
                roles,
                additionalAuthorities,
                repositoriesCredentials
        );
    }

    @Override
    public AuthenticatedUser removeRepositoryCredentials(String repositoryId) {
        return new ExternalAuthenticatedUser(
                id,
                userId,
                token,
                roles,
                additionalAuthorities,
                repositoryCredentials.values().stream().filter(cred -> !Objects.equals(repositoryId, cred.getRepository())).collect(toList())
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
