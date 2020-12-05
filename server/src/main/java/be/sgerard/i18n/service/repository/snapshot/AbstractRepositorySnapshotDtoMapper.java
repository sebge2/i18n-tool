package be.sgerard.i18n.service.repository.snapshot;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.persistence.BundleConfigurationEntity;
import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.TranslationsConfigurationEntity;
import be.sgerard.i18n.model.repository.snapshot.BaseGitRepositorySnapshotDto;
import be.sgerard.i18n.model.i18n.snapshot.BundleConfigurationSnapshotDto;
import be.sgerard.i18n.model.repository.snapshot.RepositorySnapshotDto;
import be.sgerard.i18n.model.repository.snapshot.TranslationsConfigurationSnapshotDto;
import reactor.core.publisher.Mono;

import static java.util.stream.Collectors.toList;

/**
 * Base implementation of a {@link RepositorySnapshotDtoMapper repository DTO mapper}.
 *
 * @author Sebastien Gerard
 */
public abstract class AbstractRepositorySnapshotDtoMapper<E extends RepositoryEntity, D extends RepositorySnapshotDto> implements RepositorySnapshotDtoMapper<E, D> {

    /**
     * Fills the {@link RepositoryEntity repository} based on its {@link RepositorySnapshotDto DTO} representation.
     */
    @SuppressWarnings("unchecked")
    protected Mono<E> mapFromDto(RepositoryEntity repository, BaseGitRepositorySnapshotDto dto) {
        return Mono.just(
                (E) repository
                        .setId(dto.getId())
                        .setName(dto.getName())
                        .setStatus(mapFromDto(dto.getStatus()))
                        .setTranslationsConfiguration(mapFromDto(repository.getTranslationsConfiguration(), dto.getTranslationsConfiguration()))
        );
    }

    /**
     * Fills the builder with basic information coming from the repository.
     */
    @SuppressWarnings("unchecked")
    protected Mono<D> mapToDto(RepositorySnapshotDto.Builder builder, RepositoryEntity repository) {
        return Mono.just(
                (D) builder
                        .id(repository.getId())
                        .name(repository.getName())
                        .status(mapToDto(repository.getStatus()))
                        .translationsConfiguration(mapToDto(repository.getTranslationsConfiguration()))
                        .build()
        );
    }

    /**
     * Maps the translations configuration from its DTO representation.
     */
    private TranslationsConfigurationEntity mapFromDto(TranslationsConfigurationEntity configuration, TranslationsConfigurationSnapshotDto dto) {
        return configuration
                .setBundles(dto.getBundles().stream().map(this::mapFromDto).collect(toList()))
                .setIgnoredKeys(dto.getIgnoredKeys());
    }

    /**
     * Maps the status from its DTO representation.
     */
    private RepositoryStatus mapFromDto(RepositorySnapshotDto.RepositoryStatus status) {
        switch (status) {
            case INITIALIZED:
                return RepositoryStatus.INITIALIZED;
            case NOT_INITIALIZED:
                return RepositoryStatus.NOT_INITIALIZED;
            case INITIALIZATION_ERROR:
                return RepositoryStatus.INITIALIZATION_ERROR;
            default:
                throw new UnsupportedOperationException("Unsupported status [" + status + "].");
        }
    }

    /**
     * Maps the bundle configuration from its DTO representation.
     */
    private BundleConfigurationEntity mapFromDto(BundleConfigurationSnapshotDto dto) {
        return new BundleConfigurationEntity(mapFromDto(dto.getBundleType()))
                .setIgnoredPaths(dto.getIgnoredPaths())
                .setIncludedPaths(dto.getIncludedPaths());
    }

    /**
     * Maps the bundle type from its DTO representation.
     */
    private BundleType mapFromDto(be.sgerard.i18n.model.i18n.snapshot.BundleType bundleType) {
        switch (bundleType) {
            case JAVA_PROPERTIES:
                return BundleType.JAVA_PROPERTIES;
            case JSON_ICU:
                return BundleType.JSON_ICU;
            default:
                throw new UnsupportedOperationException("Unsupported bundle type [" + bundleType + "].");
        }
    }

    /**
     * Maps the specified translation configuration to its DTO representation.
     */
    private TranslationsConfigurationSnapshotDto mapToDto(TranslationsConfigurationEntity config) {
        return TranslationsConfigurationSnapshotDto.builder()
                .ignoredKeys(config.getIgnoredKeys())
                .bundles(config.getBundles().stream().map(this::mapToDto).collect(toList()))
                .build();
    }

    /**
     * Maps the specified status to its DTO representation.
     */
    private RepositorySnapshotDto.RepositoryStatus mapToDto(RepositoryStatus status) {
        switch (status) {
            case INITIALIZED:
                return RepositorySnapshotDto.RepositoryStatus.INITIALIZED;
            case NOT_INITIALIZED:
                return RepositorySnapshotDto.RepositoryStatus.NOT_INITIALIZED;
            case INITIALIZATION_ERROR:
                return RepositorySnapshotDto.RepositoryStatus.INITIALIZATION_ERROR;
            default:
                throw new UnsupportedOperationException("Unsupported status [" + status + "].");
        }
    }

    /**
     * Maps the specified bundle configuration to its DTO representation.
     */
    private BundleConfigurationSnapshotDto mapToDto(BundleConfigurationEntity config) {
        return BundleConfigurationSnapshotDto.builder()
                .bundleType(mapToDto(config.getBundleType()))
                .ignoredPaths(config.getIgnoredPaths())
                .includedPaths(config.getIncludedPaths())
                .build();
    }

    /**
     * Maps the specified bundle type to its DTO representation.
     */
    private be.sgerard.i18n.model.i18n.snapshot.BundleType mapToDto(BundleType bundleType) {
        switch (bundleType) {
            case JAVA_PROPERTIES:
                return be.sgerard.i18n.model.i18n.snapshot.BundleType.JAVA_PROPERTIES;
            case JSON_ICU:
                return be.sgerard.i18n.model.i18n.snapshot.BundleType.JSON_ICU;
            default:
                throw new UnsupportedOperationException("Unsupported bundle type [" + bundleType + "].");
        }
    }
}
