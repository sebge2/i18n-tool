package be.sgerard.i18n.repository.user;

import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * @author Sebastien Gerard
 */
@Repository
public interface UserRepository extends CrudRepository<UserEntity, String> {

    @Override
    Collection<UserEntity> findAll();
}
