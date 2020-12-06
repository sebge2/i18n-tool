package be.sgerard.i18n.service.workspace.snapshot;

import be.sgerard.i18n.model.workspace.persistence.AbstractReviewEntity;
import be.sgerard.i18n.model.workspace.persistence.GitHubReviewEntity;
import be.sgerard.i18n.model.workspace.snapshot.AbstractReviewSnapshotDto;
import be.sgerard.i18n.model.workspace.snapshot.GitHubReviewSnapshotDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link ReviewSnapshotDtoMapper Review mapper} for GitHub.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubReviewSnapshotDtoMapper implements ReviewSnapshotDtoMapper<GitHubReviewEntity, GitHubReviewSnapshotDto> {

    public GitHubReviewSnapshotDtoMapper() {
    }

    @Override
    public boolean support(AbstractReviewSnapshotDto dto) {
        return dto instanceof GitHubReviewSnapshotDto;
    }

    @Override
    public boolean support(AbstractReviewEntity review) {
        return review instanceof GitHubReviewEntity;
    }

    @Override
    public Mono<GitHubReviewEntity> mapFromDto(GitHubReviewSnapshotDto dto) {
        return Mono.just(
                new GitHubReviewEntity(dto.getPullRequestBranch(), dto.getPullRequestNumber())
        );
    }

    @Override
    public Mono<GitHubReviewSnapshotDto> mapToDto(GitHubReviewEntity entity) {
        return Mono.just(
                GitHubReviewSnapshotDto.builder()
                        .pullRequestBranch(entity.getPullRequestBranch())
                        .pullRequestNumber(entity.getPullRequestNumber())
                        .build()
        );
    }
}
