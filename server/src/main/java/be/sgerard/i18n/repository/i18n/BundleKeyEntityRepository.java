package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationModificationEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * {@link ReactiveMongoRepository Repository} of {@link BundleKeyEntity translation bundle keys}.
 *
 * @author Sebastien Gerard
 */
public interface BundleKeyEntityRepository extends ReactiveMongoRepository<BundleKeyEntity, String>, BundleKeyEntityRepositoryCustom {

    /**
     * @see BundleKeyEntity#getWorkspace()
     */
    String FIELD_WORKSPACE = "workspace";

    /**
     * @see BundleKeyEntity#getSortingKey()
     */
    String FIELD_SORTING_KEY = "sortingKey";

    /**
     * @see BundleKeyEntity#getKey()
     */
    String FIELD_BUNDLE_KEY = "key";

    /**
     * @see BundleKeyEntity#getBundleFile()
     */
    String FIELD_BUNDLE_FILE = "bundleFile";

    /**
     * @see BundleKeyTranslationEntity#getModification()
     */
    String TRANSLATION_FIELD_MODIFICATION = "modification";

    /**
     * @see BundleKeyTranslationEntity#getOriginalValue()
     */
    String TRANSLATION_FIELD_ORIGINAL_VALUE = "originalValue";

    /**
     * @see BundleKeyTranslationEntity#getModification()
     * @see BundleKeyTranslationModificationEntity#getUpdatedValue()
     */
    String TRANSLATION_FIELD_UPDATED_VALUE = "modification.updatedValue";

    /**
     * @see BundleKeyTranslationEntity#getModification()
     * @see BundleKeyTranslationModificationEntity#getLastEditor()
     */
    String TRANSLATION_FIELD_LAST_EDITOR = "modification.lastEditor";

    /**
     * @see BundleKeyTranslationEntity#getIndex()
     */
    String TRANSLATION_FIELD_INDEX = "index";

    /**
     * Returns the sub-field of the translation associated to the specified locale.
     */
    static String getTranslationField(String localeId, String subField){
        return String.format("translations.%s.%s", localeId, subField);
    }

    /**
     * Removes all translations of a workspace.
     */
    Mono<Void> deleteByWorkspace(String workspaceId);
}
