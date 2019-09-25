package be.sgerard.i18n.service.i18n.persistence;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Sebastien Gerard
 */
@Repository
public interface BundleKeyTranslationRepository extends CrudRepository<BundleKeyTranslationEntity, String>, BundleKeyTranslationRepositoryCustom {

}
