package be.sgerard.i18n.service.security.user;

import be.sgerard.i18n.model.security.user.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Sebastien Gerard
 */
@Repository
public interface UserRepository extends CrudRepository<UserEntity, String> {

}
