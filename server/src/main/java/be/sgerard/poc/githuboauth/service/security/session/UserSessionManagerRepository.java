package be.sgerard.poc.githuboauth.service.security.session;

import be.sgerard.poc.githuboauth.model.security.session.UserSessionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
@Repository
public interface UserSessionManagerRepository extends CrudRepository<UserSessionEntity, String> {

    Optional<UserSessionEntity> findBySimpSessionId(String id);

    Collection<UserSessionEntity> findByLogoutTimeIsNull();
}
