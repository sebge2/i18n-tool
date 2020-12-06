package be.sgerard.i18n.repository.user;

import be.sgerard.i18n.model.user.persistence.ExternalUserEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * {@link ReactiveMongoRepository Repository} of {@link ExternalUserEntity external users}.
 *
 * @author Sebastien Gerard
 */
public interface ExternalUserRepository extends ReactiveMongoRepository<ExternalUserEntity, String> {

    /**
     * Finds the user having the specified {@link ExternalUserEntity#getExternalId() external id}.
     */
    Mono<ExternalUserEntity> findByExternalId(String externalId);
}
