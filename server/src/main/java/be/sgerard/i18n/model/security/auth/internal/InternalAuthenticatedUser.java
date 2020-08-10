package be.sgerard.i18n.model.security.auth.internal;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

import static be.sgerard.i18n.service.security.UserRole.mapToAuthorities;
import static java.util.stream.Collectors.toMap;

/**
 * {@link AuthenticatedUser Authenticated internal user}.
 *
 * @author Sebastien Gerard
 */
public class InternalAuthenticatedUser implements AuthenticatedUser {

    private final String id;
    private final String userId;
    private final Set<UserRole> roles;
    private final Collection<GrantedAuthority> additionalAuthorities;
    private final Map<String, RepositoryCredentials> repositoryCredentials;

    public InternalAuthenticatedUser(String id,
                                     String userId,
                                     Collection<UserRole> roles,
                                     Collection<GrantedAuthority> additionalAuthorities,
                                     Collection<RepositoryCredentials> repositoryCredentials) {
        this.id = id;
        this.userId = userId;
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
    public String getName() {
        return getUserId();
    }

    @Override
    public Collection<UserRole> getRoles() {
        return roles;
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
                additionalAuthorities,
                repositoryCredentials.values()
        );
    }

    @Override
    public AuthenticatedUser updateRepositoryCredentials(RepositoryCredentials repositoryCredentials) {
        final Set<RepositoryCredentials> repositoriesCredentials = new HashSet<>(this.repositoryCredentials.values());
        repositoriesCredentials.add(repositoryCredentials);

        return new InternalAuthenticatedUser(
                id,
                userId,
                roles,
                additionalAuthorities,
                repositoriesCredentials
        );
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
