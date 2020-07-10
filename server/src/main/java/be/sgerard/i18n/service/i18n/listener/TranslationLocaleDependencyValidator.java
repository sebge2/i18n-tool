package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.i18n.BundleKeyTranslationRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link TranslationLocaleListener Validator} that there is no translation dependencies.
 *
 * @author Sebastien Gerard
 */
@Component
public class TranslationLocaleDependencyValidator implements TranslationLocaleListener {

    /**
     * Validation message key specifying that the locale cannot be modified because there are translations
     * relying on this locale.
     */
    public static final String LOCALE_DEPENDENCIES = "validation.locale.translations-dependencies";

    private final BundleKeyTranslationRepository repository;

    public TranslationLocaleDependencyValidator(BundleKeyTranslationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(TranslationLocaleEntity original, TranslationLocaleDto update) {
        return validateNoDependency(original);
    }

    @Override
    public Mono<ValidationResult> beforeDelete(TranslationLocaleEntity locale) {
        return validateNoDependency(locale);
    }

    /**
     * Validates the specified locale.
     */
    private Mono<ValidationResult> validateNoDependency(TranslationLocaleEntity locale) {
        return repository
                .existsByLocale(locale.getId())
                .map(exists ->
                        exists
                                ? ValidationResult.builder()
                                .messages(new ValidationMessage(LOCALE_DEPENDENCIES))
                                .build()
                                : ValidationResult.EMPTY
                );
    }
}
