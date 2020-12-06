package be.sgerard.i18n.service.repository.snapshot;

import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.repository.snapshot.GitHubRepositorySnapshotDto;
import be.sgerard.i18n.model.repository.snapshot.RepositorySnapshotDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link BaseGitRepositorySnapshotDtoMapper DTO} mapper for GitHub repositories.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubRepositorySnapshotDtoMapper extends BaseGitRepositorySnapshotDtoMapper<GitHubRepositoryEntity, GitHubRepositorySnapshotDto> {

    @Override
    public boolean support(RepositorySnapshotDto dto) {
        return dto instanceof GitHubRepositorySnapshotDto;
    }

    @Override
    public boolean support(RepositoryEntity repository) {
        return repository instanceof GitHubRepositoryEntity;
    }

    @Override
    public Mono<GitHubRepositoryEntity> mapFromDto(GitHubRepositorySnapshotDto dto) {
        return mapFromDto(
                new GitHubRepositoryEntity(dto.getUsername(), dto.getRepository())
                        .setAccessKey(dto.getAccessKey().orElse(null))
                        .setWebHookSecret(dto.getWebHookSecret().orElse(null)),
                dto
        );
    }

    @Override
    public Mono<GitHubRepositorySnapshotDto> mapToDto(GitHubRepositoryEntity repository) {
        return mapToDto(
                GitHubRepositorySnapshotDto.builder()
                        .username(repository.getUsername())
                        .repository(repository.getRepository())
                        .accessKey(repository.getAccessKey().orElse(null))
                        .webHookSecret(repository.getWebHookSecret().orElse(null)),
                repository
        );
    }
}
