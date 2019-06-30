package be.sgerard.poc.githuboauth.service.i18n;

import be.sgerard.poc.githuboauth.model.i18n.TranslationWorkspaceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
@Repository
public interface TranslationWorkspaceRepository extends CrudRepository<TranslationWorkspaceEntity, String> {

    List<TranslationWorkspaceEntity> findAll();

}
