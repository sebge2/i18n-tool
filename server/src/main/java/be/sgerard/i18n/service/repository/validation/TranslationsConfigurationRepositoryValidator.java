package be.sgerard.i18n.service.repository.validation;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.persistence.BundleConfigurationEntity;
import be.sgerard.i18n.model.repository.dto.BundleConfigurationPatchDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.dto.TranslationsConfigurationPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * {@link RepositoryValidator Repository validator} checking that translations configuration.
 *
 * @author Sebastien Gerard
 */
@Component
public class TranslationsConfigurationRepositoryValidator implements RepositoryValidator<RepositoryEntity> {

    public TranslationsConfigurationRepositoryValidator() {
    }

    @Override
    public boolean support(RepositoryEntity repositoryEntity) {
        return true;
    }

    @Override
    public Mono<ValidationResult> beforePersist(RepositoryEntity repository) {
        return Mono.just(ValidationResult.merge(
                validateNoNull(
                        repository.getTranslationsConfiguration().getIgnoredKeys()
                ),
                validateNoNullAntPaths(
                        repository.getTranslationsConfiguration()
                                .getBundle(BundleType.JAVA_PROPERTIES)
                                .map(BundleConfigurationEntity::getIgnoredPaths)
                                .orElse(emptyList())
                ),
                validateNoNullAntPaths(
                        repository.getTranslationsConfiguration()
                                .getBundle(BundleType.JAVA_PROPERTIES)
                                .map(BundleConfigurationEntity::getIncludedPaths)
                                .orElse(emptyList())
                ),
                validateNoNullAntPaths(
                        repository.getTranslationsConfiguration()
                                .getBundle(BundleType.JSON_ICU)
                                .map(BundleConfigurationEntity::getIgnoredPaths)
                                .orElse(emptyList())
                ),
                validateNoNullAntPaths(
                        repository.getTranslationsConfiguration()
                                .getBundle(BundleType.JSON_ICU)
                                .map(BundleConfigurationEntity::getIncludedPaths)
                                .orElse(emptyList())
                )
        ));
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(RepositoryEntity original, RepositoryPatchDto patch) {
        return Mono.just(ValidationResult.merge(
                validateNoNull(
                        patch.getTranslationsConfiguration()
                                .flatMap(TranslationsConfigurationPatchDto::getIgnoredKeys)
                                .orElse(emptyList())
                ),
                validateNoNullAntPaths(
                        patch.getTranslationsConfiguration()
                                .flatMap(TranslationsConfigurationPatchDto::getJavaProperties)
                                .flatMap(BundleConfigurationPatchDto::getIgnoredPaths)
                                .orElse(emptyList())
                ),
                validateNoNullAntPaths(
                        patch.getTranslationsConfiguration()
                                .flatMap(TranslationsConfigurationPatchDto::getJavaProperties)
                                .flatMap(BundleConfigurationPatchDto::getIncludedPaths)
                                .orElse(emptyList())
                ),
                validateNoNullAntPaths(
                        patch.getTranslationsConfiguration()
                                .flatMap(TranslationsConfigurationPatchDto::getJsonIcu)
                                .flatMap(BundleConfigurationPatchDto::getIgnoredPaths)
                                .orElse(emptyList())
                ),
                validateNoNullAntPaths(
                        patch.getTranslationsConfiguration()
                                .flatMap(TranslationsConfigurationPatchDto::getJsonIcu)
                                .flatMap(BundleConfigurationPatchDto::getIgnoredPaths)
                                .orElse(emptyList())
                )
        ));
    }

    /**
     * Checks that the specified values are not <tt>null</tt>.
     */
    private ValidationResult validateNoNull(List<String> values) {
        if (values.contains(null)) {
            return ValidationResult.singleMessage(new ValidationMessage("validation.repository.ignored-key-null"));
        }

        return ValidationResult.EMPTY;
    }

    /**
     * Checks that the specified values are not <tt>null</tt>.
     */
    private ValidationResult validateNoNullAntPaths(List<String> values) {
        if (values.contains(null)) {
            return ValidationResult.singleMessage(new ValidationMessage("validation.repository.included-excluded-bundle-path-invalid"));
        }

        return ValidationResult.EMPTY;
    }
}
