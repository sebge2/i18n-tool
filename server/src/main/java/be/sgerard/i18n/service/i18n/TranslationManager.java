package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.dto.BundleKeysPageDto;
import be.sgerard.i18n.model.i18n.dto.BundleKeysPageRequestDto;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Sebastien Gerard
 */
public interface TranslationManager {

    Mono<Void> readTranslations(WorkspaceEntity workspaceEntity, TranslationRepositoryReadApi api);

    void writeTranslations(WorkspaceEntity workspaceEntity, TranslationRepositoryWriteApi api);

    BundleKeysPageDto getTranslations(BundleKeysPageRequestDto searchRequest) throws ResourceNotFoundException;

    void updateTranslations(WorkspaceEntity workspaceEntity, Map<String, String> translations) throws ResourceNotFoundException;
}
