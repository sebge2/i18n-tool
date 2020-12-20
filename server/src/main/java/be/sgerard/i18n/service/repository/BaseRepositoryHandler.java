package be.sgerard.i18n.service.repository;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.persistence.BundleConfigurationEntity;
import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.BundleConfigurationPatchDto;
import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.dto.TranslationsConfigurationPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.TranslationsConfigurationEntity;
import be.sgerard.i18n.model.security.repository.RepositoryCredentials;
import reactor.core.publisher.Mono;

import static java.util.stream.Collectors.toList;

/**
 * Base implementation of a {@link RepositoryHandler repository handler}.
 *
 * @author Sebastien Gerard
 */
public abstract class BaseRepositoryHandler<E extends RepositoryEntity, C extends RepositoryCreationDto, P extends RepositoryPatchDto, D extends RepositoryCredentials> implements RepositoryHandler<E, C, P, D> {

    private final RepositoryType supportedType;

    protected BaseRepositoryHandler(RepositoryType supportedType) {
        this.supportedType = supportedType;
    }

    @Override
    public boolean support(RepositoryType type) {
        return supportedType == type;
    }

    @Override
    public final Mono<E> updateRepository(E repository, P patchDto, D credentials) throws RepositoryException {
        patchDto.getTranslationsConfiguration().ifPresent(config -> updateRepository(repository, config));

        return updateFromPatch(patchDto, repository);
    }

    /**
     * Updates the repository based on the specified patch.
     */
    protected abstract Mono<E> updateFromPatch(P patchDto, E repository);

    /**
     * Updates the {@link RepositoryEntity#getTranslationsConfiguration() translation configuration} using the specified {@link TranslationsConfigurationPatchDto patch}.
     */
    private void updateRepository(E repository, TranslationsConfigurationPatchDto patch) {
        final TranslationsConfigurationEntity translationsConfiguration = repository.getTranslationsConfiguration();

        patch.getIgnoredKeys().map(values -> values.stream().distinct().collect(toList())).ifPresent(translationsConfiguration::setIgnoredKeys);

        patch.getJavaProperties().ifPresent(bundlePath -> updateConfiguration(translationsConfiguration.getBundleOrCreate(BundleType.JAVA_PROPERTIES), bundlePath));
        patch.getJsonIcu().ifPresent(bundlePath -> updateConfiguration(translationsConfiguration.getBundleOrCreate(BundleType.JSON_ICU), bundlePath));
    }

    /**
     * Updates the {@link BundleConfigurationEntity bundle configuration} based on the specified {@link BundleConfigurationPatchDto patch}.
     */
    private void updateConfiguration(BundleConfigurationEntity bundleConfiguration, BundleConfigurationPatchDto bundlePath) {
        bundlePath.getIgnoredPaths()
                .map(values -> values.stream().distinct().collect(toList()))
                .ifPresent(bundleConfiguration::setIgnoredPaths);
        bundlePath.getIncludedPaths()
                .map(values -> values.stream().distinct().collect(toList()))
                .ifPresent(bundleConfiguration::setIncludedPaths);
    }
}
