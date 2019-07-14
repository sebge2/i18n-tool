package be.sgerard.poc.githuboauth.service.i18n.persistence;

import be.sgerard.poc.githuboauth.model.i18n.persistence.WorkspaceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
@Repository
public interface WorkspaceRepository extends CrudRepository<WorkspaceEntity, String> {

    List<WorkspaceEntity> findAll();

}
