package be.sgerard.i18n.repository.user;

import be.sgerard.i18n.model.security.user.persistence.InternalUserEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * {@link ReactiveMongoRepository Repository} of {@link InternalUserEntity internal users}.
 *
 * @author Sebastien Gerard
 */
public interface InternalUserRepository extends ReactiveMongoRepository<InternalUserEntity, String> {

    /**
     * Finds the user having the specified {@link InternalUserEntity#getUsername() username}.
     */
    Mono<InternalUserEntity> findByUsername(String username);

}
