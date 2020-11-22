package be.sgerard.i18n.service.repository.snapshot;

import be.sgerard.i18n.model.repository.persistence.BaseGitRepositoryEntity;
import be.sgerard.i18n.model.repository.snapshot.BaseGitRepositorySnapshotDto;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Base implementation of a {@link RepositorySnapshotDtoMapper Git repository DTO mapper}.
 *
 * @author Sebastien Gerard
 */
public abstract class BaseGitRepositorySnapshotDtoMapper<E extends BaseGitRepositoryEntity, D extends BaseGitRepositorySnapshotDto>
        extends AbstractRepositorySnapshotDtoMapper<E, D> {

    /**
     * Fills the {@link BaseGitRepositoryEntity repository} based on its {@link BaseGitRepositorySnapshotDto DTO} representation.
     */
    protected Mono<E> mapFromDto(BaseGitRepositoryEntity repository, BaseGitRepositorySnapshotDto dto) {
        return super.mapFromDto(
                repository
                        .setLocation(dto.getLocation())
                        .setDefaultBranch(dto.getDefaultBranch())
                        .setAllowedBranches(Optional.ofNullable(dto.getAllowedBranches()).map(Pattern::compile).orElse(repository.getAllowedBranches())),
                dto
        );
    }

    /**
     * Fills the builder with basic information coming from the repository.
     */
    protected Mono<D> mapToDto(BaseGitRepositorySnapshotDto.Builder builder, BaseGitRepositoryEntity repository) {
        return super.mapToDto(
                builder
                        .location(repository.getLocation())
                        .defaultBranch(repository.getDefaultBranch())
                        .allowedBranches(repository.getAllowedBranches().toString()),
                repository
        );
    }
}
