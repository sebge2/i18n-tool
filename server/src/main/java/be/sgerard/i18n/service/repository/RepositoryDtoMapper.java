package be.sgerard.i18n.service.repository;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.persistence.BundleConfigurationEntity;
import be.sgerard.i18n.model.repository.dto.*;
import be.sgerard.i18n.model.repository.persistence.*;
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
        final TranslationsConfigurationEntity translationsConfig = repository.getTranslationsConfiguration();
        final BundleConfigurationEntity javaBundleConfig = translationsConfig.getBundleOrCreate(BundleType.JAVA_PROPERTIES);
        final BundleConfigurationEntity jsonBundleConfig = translationsConfig.getBundleOrCreate(BundleType.JSON_ICU);

        return (B) builder
                .id(repository.getId())
                .name(repository.getName())
                .status(repository.getStatus())
                .defaultBranch(repository.getDefaultBranch())
                .allowedBranches(repository.getAllowedBranches().toString())
                .location(repository.getLocation())
                .translationsConfiguration(
                        TranslationsConfigurationDto.builder()
                                .ignoredKeys(translationsConfig.getIgnoredKeys())
                                .javaProperties(mapToDto(javaBundleConfig))
                                .jsonIcu(mapToDto(jsonBundleConfig))
                                .build()
                );
    }

    /**
     * Maps the specified {@link BundleConfigurationEntity bundle configuration entity} to its {@link BundleConfigurationDto DTO representation}.
     */
    private BundleConfigurationDto mapToDto(BundleConfigurationEntity bundleConfig) {
        return BundleConfigurationDto.builder()
                .includedPaths(bundleConfig.getIncludedPaths())
                .ignoredPaths(bundleConfig.getIgnoredPaths())
                .build();
    }
}
