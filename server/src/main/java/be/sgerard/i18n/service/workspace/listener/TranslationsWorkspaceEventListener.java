package be.sgerard.i18n.service.workspace.listener;

import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;

/**
 * @author Sebastien Gerard
 */
public class TranslationsWorkspaceEventListener implements WorkspaceListener {

    // TODO delete translations

    @Override
    public boolean support(WorkspaceEntity workspace) {
        return true;
    }
}
