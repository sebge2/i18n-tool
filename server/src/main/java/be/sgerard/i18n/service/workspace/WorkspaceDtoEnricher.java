package be.sgerard.i18n.service.workspace;

import be.sgerard.i18n.model.workspace.dto.WorkspaceDto;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import org.springframework.stereotype.Component;

/**
 * Enricher of {@link be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity workspace entities}
 * to get a {@link be.sgerard.i18n.model.workspace.dto.WorkspaceDto workspace DTO.
 *
 * @author Sebastien Gerard
 */
@Component
public class WorkspaceDtoEnricher {

    public WorkspaceDtoEnricher() {
    }

    /**
     * Maps the entity and enriches it.
     */
    public WorkspaceDto mapAndEnrich(WorkspaceEntity workspace) {
        return WorkspaceDto.builder()
                .id(workspace.getId())
                .branch(workspace.getBranch())
                .status(workspace.getStatus())
                .repositoryId(workspace.getRepository())
                .build();
    }
}
