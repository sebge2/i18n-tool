package be.sgerard.i18n.repository.repository;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * {@link ReactiveMongoRepository} of {@link RepositoryEntity repository entities}.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryEntityRepository extends ReactiveMongoRepository<RepositoryEntity, String> {

}
