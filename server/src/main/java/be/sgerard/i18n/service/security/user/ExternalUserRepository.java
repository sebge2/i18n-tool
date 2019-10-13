package be.sgerard.i18n.service.security.user;

import be.sgerard.i18n.model.security.user.ExternalUserEntity;
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
