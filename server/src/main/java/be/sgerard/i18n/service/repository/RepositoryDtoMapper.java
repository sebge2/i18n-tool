package be.sgerard.i18n.service.repository;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryDto;
import be.sgerard.i18n.model.repository.dto.GitRepositoryDto;
import be.sgerard.i18n.model.repository.dto.RepositoryDto;
import be.sgerard.i18n.model.repository.persistence.BaseGitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for the {@link RepositoryDto repository DTO}.
 *
 * @author Sebastien Gerard
 */
@Component
public class RepositoryDtoMapper {

    public RepositoryDtoMapper() {
    }

    /**
     * Maps the specified entity to a DTO.
     */
    public RepositoryDto mapToDto(RepositoryEntity entity) {
        if (entity instanceof GitHubRepositoryEntity) {
            final GitHubRepositoryEntity gitHubRepositoryEntity = (GitHubRepositoryEntity) entity;

            return fillBuilder(GitHubRepositoryDto.gitHubBuilder(), gitHubRepositoryEntity)
                    .username(gitHubRepositoryEntity.getUsername())
                    .repository(gitHubRepositoryEntity.getRepository())
                    .build();
        } else if (entity instanceof GitRepositoryEntity) {
            return fillBuilder(GitRepositoryDto.gitBuilder(), (GitRepositoryEntity) entity)
                    .build();
        } else {
            throw new UnsupportedOperationException("Unsupported entity [" + entity + "].");
        }
    }

    /**
     * Fills the builder with the specified entity.
     */
    @SuppressWarnings("unchecked")
    private <B extends GitRepositoryDto.BaseBuilder<?, ?>> B fillBuilder(B builder, BaseGitRepositoryEntity repository) {
        return (B) builder
                .id(repository.getId())
                .name(repository.getName())
                .status(repository.getStatus())
                .defaultBranch(repository.getDefaultBranch())
                .allowedBranches(repository.getAllowedBranches().toString())
                .location(repository.getLocation());
    }
}
