package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.dto.WorkspaceDto;
import be.sgerard.i18n.model.i18n.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.event.EventService;
import org.springframework.stereotype.Component;

import static be.sgerard.i18n.model.event.EventType.*;

/**
 * {@link WorkspaceListener Workspace listener} emitting events when the workspace changes.
 *
 * @author Sebastien Gerard
 */
@Component
public class EventWorkspaceListener implements WorkspaceListener {

    private final EventService eventService;

    public EventWorkspaceListener(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public boolean support(WorkspaceEntity workspace) {
        return true;
    }

    @Override
    public void onCreate(WorkspaceEntity workspace) {
        eventService.broadcastEvent(ADDED_WORKSPACE, WorkspaceDto.builder(workspace).build());
    }

    @Override
    public void onInitialize(WorkspaceEntity workspace) {
        eventService.broadcastEvent(UPDATED_WORKSPACE, WorkspaceDto.builder(workspace).build());
    }

    @Override
    public void onDelete(WorkspaceEntity workspace) {
        eventService.broadcastEvent(DELETED_WORKSPACE, WorkspaceDto.builder(workspace).build());
    }
}
