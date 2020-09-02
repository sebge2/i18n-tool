package be.sgerard.i18n.service.workspace.listener;

import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.repository.i18n.BundleKeyEntityRepository;
import be.sgerard.i18n.service.workspace.listener.WorkspaceListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link WorkspaceListener Listener} for events associated to the lifecycle of translations in a workspace.
 *
 * @author Sebastien Gerard
 */
@Component
@Order(0)
public class TranslationsWorkspaceListener implements WorkspaceListener {

    private final BundleKeyEntityRepository translationRepository;

    public TranslationsWorkspaceListener(BundleKeyEntityRepository translationRepository) {
        this.translationRepository = translationRepository;
    }

    @Override
    public boolean support(WorkspaceEntity workspace) {
        return true;
    }

    @Override
    public Mono<Void> onDelete(WorkspaceEntity workspace) {
        return translationRepository.deleteByWorkspace(workspace.getId());
    }
}
