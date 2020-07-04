package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * {@link ReactiveMongoRepository Repository} of {@link BundleKeyTranslationEntity translation entities}.
 *
 * @author Sebastien Gerard
 */
public interface BundleKeyTranslationRepository extends ReactiveMongoRepository<BundleKeyTranslationEntity, String>, BundleKeyTranslationRepositoryCustom {

    /**
     * @see BundleKeyTranslationEntity#getId()
     */
    String FIELD_ID = "id";

    /**
     * @see BundleKeyTranslationEntity#getWorkspace()
     */
    String FIELD_WORKSPACE = "workspace";

    /**
     * @see BundleKeyTranslationEntity#getOriginalValue()
     */
    String FIELD_ORIGINAL_VALUE = "originalValue";

    /**
     * @see BundleKeyTranslationEntity#getUpdatedValue()
     */
    String FIELD_UPDATED_VALUE = "updatedValue";

    /**
     * @see BundleKeyTranslationEntity#getLastEditor()
     */
    String FIELD_LAST_EDITOR = "lastEditor";

    /**
     * @see BundleKeyTranslationEntity#getBundleKey()
     */
    String FIELD_BUNDLE_KEY = "bundleKey";

    /**
     * @see BundleKeyTranslationEntity#getBundleFile()
     */
    String FIELD_BUNDLE_FILE = "bundleFile";

    /**
     * @see BundleKeyTranslationEntity#getLocale()
     */
    String FIELD_LOCALE = "locale";

    /**
     * @see BundleKeyTranslationEntity#getIndex() ()
     */
    String FIELD_INDEX = "index";
}
