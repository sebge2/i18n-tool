package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationModificationEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

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
     * @see BundleKeyTranslationEntity#getModification()
     */
    String FIELD_MODIFICATION = "modification";

    /**
     * @see BundleKeyTranslationEntity#getModification()
     * @see BundleKeyTranslationModificationEntity#getUpdatedValue()
     */
    String FIELD_UPDATED_VALUE = "modification.updatedValue";

    /**
     * @see BundleKeyTranslationEntity#getModification()
     * @see BundleKeyTranslationModificationEntity#getLastEditor()
     */
    String FIELD_LAST_EDITOR = "modification.lastEditor";

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

    /**
     * Returns whether a translation exists with that locale.
     */
    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    Mono<Boolean> existsByLocale(String locale);
}
