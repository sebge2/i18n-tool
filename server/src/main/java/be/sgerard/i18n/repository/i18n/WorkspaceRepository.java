package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.persistence.WorkspaceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
@Repository
public interface WorkspaceRepository extends CrudRepository<WorkspaceEntity, String> {

    List<WorkspaceEntity> findAll();

    Optional<WorkspaceEntity> findByPullRequestNumber(int requestNumber);

    Optional<WorkspaceEntity> findByBranch(String branch);

}
