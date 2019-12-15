package be.sgerard.i18n.model.security.auth;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.service.security.UserRole;

import java.io.Serializable;
import java.util.Collection;

/**
 * Credentials that an {@link AuthenticatedUser authenticated user} has on a particular repository.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryCredentials extends Serializable {

    /**
     * Returns the repository id.
     *
     * @see RepositoryEntity#getId()
     */
    String getRepository();

    /**
     * Returns all the {@link UserRole roles} that the user has on this repository.
     */
    Collection<UserRole> getSessionRoles();

}
