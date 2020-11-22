package be.sgerard.i18n.service.repository.snapshot;

import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.repository.snapshot.GitRepositorySnapshotDto;
import be.sgerard.i18n.model.repository.snapshot.RepositorySnapshotDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link BaseGitRepositorySnapshotDtoMapper DTO} mapper for Git repositories.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitRepositorySnapshotDtoMapper extends BaseGitRepositorySnapshotDtoMapper<GitRepositoryEntity, GitRepositorySnapshotDto> {

    @Override
    public boolean support(RepositorySnapshotDto dto) {
        return dto instanceof GitRepositorySnapshotDto;
    }

    @Override
    public boolean support(RepositoryEntity repository) {
        return repository instanceof GitRepositoryEntity;
    }

    @Override
    public Mono<GitRepositoryEntity> mapFromDto(GitRepositorySnapshotDto dto) {
        return mapFromDto(
                new GitRepositoryEntity(dto.getName(), dto.getLocation())
                        .setUsername(dto.getUsername().orElse(null))
                        .setPassword(dto.getPassword().orElse(null)),
                dto
        );
    }

    @Override
    public Mono<GitRepositorySnapshotDto> mapToDto(GitRepositoryEntity repository) {
        return mapToDto(
                GitRepositorySnapshotDto.builder()
                        .username(repository.getUsername().orElse(null))
                        .password(repository.getPassword().orElse(null)),
                repository
        );
    }
}
