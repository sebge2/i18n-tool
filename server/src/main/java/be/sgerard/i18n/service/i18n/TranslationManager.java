package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Manager of translations found in bundle files.
 *
 * @author Sebastien Gerard
 */
public interface TranslationManager {

    /**
     * Reads all the translations of the specified {@link WorkspaceEntity workspace} using the {@link TranslationRepositoryReadApi read API}.
     */
    Flux<BundleFileEntity> readTranslations(WorkspaceEntity workspace, TranslationRepositoryReadApi api);

    /**
     * Writes back all the translations using the specified {@link WorkspaceEntity workspace} using the
     * {@link TranslationRepositoryWriteApi write API}.
     */
    Mono<Void> writeTranslations(WorkspaceEntity workspace, TranslationRepositoryWriteApi api);

    /**
     * Updates the translation of the specified {@link BundleKeyEntity#getId() bundle key} for the specified locale.
     */
    Mono<BundleKeyEntity> updateTranslation(String bundleKeyId, String localeId, String translation) throws ResourceNotFoundException;
}
