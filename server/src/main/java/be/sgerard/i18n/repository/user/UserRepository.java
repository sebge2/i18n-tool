package be.sgerard.i18n.repository.user;

import be.sgerard.i18n.model.user.persistence.UserEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * {@link ReactiveMongoRepository Repository} of {@link UserEntity users}.
 *
 * @author Sebastien Gerard
 */
public interface UserRepository extends ReactiveMongoRepository<UserEntity, String> {

}
