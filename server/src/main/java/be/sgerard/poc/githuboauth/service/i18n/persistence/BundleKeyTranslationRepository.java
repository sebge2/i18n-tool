package be.sgerard.poc.githuboauth.service.i18n.persistence;

import be.sgerard.poc.githuboauth.model.i18n.persistence.BundleKeyTranslationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Sebastien Gerard
 */
@Repository
public interface BundleKeyTranslationRepository extends CrudRepository<BundleKeyTranslationEntity, String>, BundleKeyTranslationRepositoryCustom {

}
