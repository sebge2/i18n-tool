package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

/**
 * {@link CrudRepository Repository} of {@link WorkspaceEntity workspace}.
 *
 * @author Sebastien Gerard
 */
@Repository
public interface WorkspaceRepository extends CrudRepository<WorkspaceEntity, String> {

    /**
     * Finds all {@link WorkspaceEntity workspaces}.
     */
    List<WorkspaceEntity> findAll();

    /**
     * Finds {@link WorkspaceEntity workspaces} of the specified repository.
     */
    Stream<WorkspaceEntity> findByRepositoryId(String repositoryId);

}
