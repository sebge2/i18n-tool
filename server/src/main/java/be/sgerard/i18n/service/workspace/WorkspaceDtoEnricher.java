package be.sgerard.i18n.service.workspace;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.workspace.dto.WorkspaceDto;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.repository.RepositoryManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Enricher of {@link be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity workspace entities}
 * to get a {@link be.sgerard.i18n.model.workspace.dto.WorkspaceDto workspace DTO.
 *
 * @author Sebastien Gerard
 */
@Component
public class WorkspaceDtoEnricher {

    private final RepositoryManager repositoryManager;

    public WorkspaceDtoEnricher(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    /**
     * Maps the entity and enriches it.
     */
    public Mono<WorkspaceDto> mapAndEnrich(WorkspaceEntity workspace) {
        return repositoryManager
                .findById(workspace.getRepository())
                .map(repository -> doMapAndEnrich(workspace, repository))
                .switchIfEmpty(Mono.defer(() -> Mono.just(doMapAndEnrich(workspace, null))));
    }

    /**
     * Does the mapping and enrichment.
     */
    private WorkspaceDto doMapAndEnrich(WorkspaceEntity workspace, RepositoryEntity repository) {
        final Optional<RepositoryEntity> nullableRepo = Optional.ofNullable(repository);

        return WorkspaceDto.builder()
                .id(workspace.getId())
                .branch(workspace.getBranch())
                .defaultWorkspace(nullableRepo.map(rep -> rep.isDefaultBranch(workspace.getBranch())).orElse(false))
                .status(workspace.getStatus())
                .repositoryId(workspace.getRepository())
                .repositoryName(nullableRepo.map(RepositoryEntity::getName).orElse(null))
                .repositoryType(nullableRepo.map(RepositoryEntity::getType).orElse(null))
                .repositoryStatus(nullableRepo.map(RepositoryEntity::getStatus).orElse(null))
                .build();
    }
}