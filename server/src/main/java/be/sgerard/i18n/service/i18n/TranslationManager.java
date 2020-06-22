package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Manager of translations found in bundle files.
 *
 * @author Sebastien Gerard
 */
public interface TranslationManager {
    /**
     * Finds the {@link BundleKeyTranslationEntity translation} having the specified id.
     */
    Mono<BundleKeyTranslationEntity> findTranslation(String id);

    /**
     * Finds the {@link BundleKeyTranslationEntity translation} having the specified id.
     */
    default Mono<BundleKeyTranslationEntity> findTranslationOrDie(String id) {
        return findTranslation(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.translationNotFoundException(id)));
    }

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
     * Updates translations of the specified {@link WorkspaceEntity workspace}, the map associates the
     * {@link BundleKeyTranslationEntity#getId() translation id} to the actual translation value.
     */
    Flux<BundleKeyTranslationEntity> updateTranslations(Map<String, String> translations) throws ResourceNotFoundException;
}
