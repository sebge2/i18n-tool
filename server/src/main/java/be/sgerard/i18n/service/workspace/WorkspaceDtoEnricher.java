package be.sgerard.i18n.service.workspace;

import be.sgerard.i18n.model.i18n.dto.TranslationSearchCriterion;
import be.sgerard.i18n.model.i18n.dto.TranslationsSearchRequestDto;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.workspace.dto.WorkspaceDto;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.i18n.TranslationSearchManager;
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
    private final TranslationSearchManager translationSearchManager;

    public WorkspaceDtoEnricher(RepositoryManager repositoryManager, TranslationSearchManager translationSearchManager) {
        this.repositoryManager = repositoryManager;
        this.translationSearchManager = translationSearchManager;
    }

    /**
     * Maps the entity and enriches it.
     */
    public Mono<WorkspaceDto> mapAndEnrich(WorkspaceEntity workspace) {
        return repositoryManager
                .findById(workspace.getRepository())
                .flatMap(repository -> doMapAndEnrich(workspace, repository))
                .switchIfEmpty(Mono.defer(() -> doMapAndEnrich(workspace, null)));
    }

    /**
     * Does the mapping and enrichment.
     */
    private Mono<WorkspaceDto> doMapAndEnrich(WorkspaceEntity workspace, RepositoryEntity repository) {
        return isDirty(workspace)
                .map(dirty -> {
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
                            .numberBundleKeys(workspace.getFiles().stream().map(BundleFileEntity::getNumberKeys).reduce(0L, Long::sum))
                            .dirty(dirty)
                            .build();
                });
    }

    /**
     * Returns whether the specified workspace has some updates.
     */
    private Mono<Boolean> isDirty(WorkspaceEntity workspace) {
        return translationSearchManager
                .hasElements(TranslationsSearchRequestDto.builder()
                        .workspace(workspace.getId())
                        .criterion(TranslationSearchCriterion.UPDATED_TRANSLATIONS)
                        .maxKeys(1)
                        .build()
                );
    }
}
