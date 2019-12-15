package be.sgerard.i18n.repository.workspace;

import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * {@link ReactiveMongoRepository Repository} of {@link WorkspaceEntity workspace}.
 *
 * @author Sebastien Gerard
 */
public interface WorkspaceRepository extends ReactiveMongoRepository<WorkspaceEntity, String> {

    /**
     * Finds {@link WorkspaceEntity workspaces} of the specified repository.
     */
    Flux<WorkspaceEntity> findByRepository(String repositoryId);

}
