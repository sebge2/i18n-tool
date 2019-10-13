package be.sgerard.i18n.service.security.user;

import be.sgerard.i18n.model.security.user.InternalUserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
@Repository
public interface InternalUserRepository extends CrudRepository<InternalUserEntity, String> {

    Optional<InternalUserEntity> findByUsername(String username);

}
