package be.sgerard.i18n.repository.security;

import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
@Repository
public interface UserLiveSessionManagerRepository extends CrudRepository<UserLiveSessionEntity, String> {

    Optional<UserLiveSessionEntity> findBySimpSessionId(String id);

    Collection<UserLiveSessionEntity> findByLogoutTimeIsNull();

    Collection<UserLiveSessionEntity> findByAuthenticatedUserId(String authenticatedUserId);

}
