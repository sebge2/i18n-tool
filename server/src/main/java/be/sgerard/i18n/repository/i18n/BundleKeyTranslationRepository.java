package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * {@link ReactiveMongoRepository Repository} of {@link BundleKeyTranslationEntity translation entities}.
 *
 * @author Sebastien Gerard
 */
public interface BundleKeyTranslationRepository extends ReactiveMongoRepository<BundleKeyTranslationEntity, String>, BundleKeyTranslationRepositoryCustom {

}
