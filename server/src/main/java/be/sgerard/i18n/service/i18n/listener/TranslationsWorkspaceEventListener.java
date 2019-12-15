package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.dto.TranslationUpdateDto;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.workspace.WorkspaceDtoEnricher;
import be.sgerard.i18n.service.workspace.WorkspaceManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static be.sgerard.i18n.model.event.EventType.UPDATED_WORKSPACE;

/**
 * {@link TranslationsListener Translations listener} emitting an event every time translations are updated
 * specifying that the workspace has been updated.
 *
 * @author Sebastien Gerard
 */
@Component
public class TranslationsWorkspaceEventListener implements TranslationsListener {

    private final EventService eventService;
    private final WorkspaceManager workspaceManager;
    private final WorkspaceDtoEnricher workspaceDtoEnricher;

    public TranslationsWorkspaceEventListener(EventService eventService,
                                              WorkspaceManager workspaceManager,
                                              WorkspaceDtoEnricher workspaceDtoEnricher) {
        this.eventService = eventService;
        this.workspaceManager = workspaceManager;
        this.workspaceDtoEnricher = workspaceDtoEnricher;
    }

    @Override
    public Mono<Void> afterUpdate(List<BundleKeyEntity> bundleKeys, List<TranslationUpdateDto> updates) {
        return Flux
                .fromStream(bundleKeys.stream().map(BundleKeyEntity::getWorkspace).distinct())
                .flatMap(workspaceManager::findByIdOrDie)
                .flatMap(workspaceDtoEnricher::mapAndEnrich)
                .flatMap(workspaceDto -> eventService.broadcastEvent(UPDATED_WORKSPACE, workspaceDto))
                .then();
    }
}
