package be.sgerard.i18n.repository.user;

import be.sgerard.i18n.model.security.user.persistence.ExternalUserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
@Repository
public interface ExternalUserRepository extends CrudRepository<ExternalUserEntity, String> {

    Optional<ExternalUserEntity> findByExternalId(String externalId);
}
