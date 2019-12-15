package be.sgerard.i18n.repository.repository;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * {@link Repository} of {@link RepositoryEntity repository entities}.
 *
 * @author Sebastien Gerard
 */
@Repository
public interface RepositoryEntityRepository extends CrudRepository<RepositoryEntity, String> {

    @Override
    Collection<RepositoryEntity> findAll();

}
