package be.sgerard.i18n.service.dictionary.validation;

import be.sgerard.i18n.model.dictionary.dto.DictionaryEntryPatchDto;
import be.sgerard.i18n.model.dictionary.persistence.DictionaryEntryEntity;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.locale.TranslationLocaleManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static be.sgerard.i18n.model.validation.ValidationResult.toValidationResult;

/**
 * {@link DictionaryValidator Validator} checking that locale ids exist.
 *
 * @author Sebastien Gerard
 */
@Component
public class DictionaryLocaleValidator implements DictionaryValidator {

    /**
     * Validation message key specifying that a translation locale is missing.
     */
    public static final String MISSING_LOCALE = "validation.locale.missing";

    private final TranslationLocaleManager localeManager;

    public DictionaryLocaleValidator(TranslationLocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    @Override
    public Mono<ValidationResult> beforePersist(DictionaryEntryEntity dictionaryEntry) {
        return validateLocalesExist(dictionaryEntry.getTranslations());
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(DictionaryEntryEntity original, DictionaryEntryPatchDto update) {
        return validateLocalesExist(update.getTranslations());
    }

    /**
     * Validates that the specified locales exist.
     */
    private Mono<ValidationResult> validateLocalesExist(Map<String, String> translations) {
        return localeManager.findAll().collectList()
                .map(availableLocales ->
                translations.keySet().stream()
                        .map(locale -> validateLocaleExist(locale, availableLocales))
                        .collect(toValidationResult())
                );
    }

    /**
     * Validates that the specified locale exists.
     */
    private ValidationResult validateLocaleExist(String locale, List<TranslationLocaleEntity> availableLocales) {
        return availableLocales.stream()
                .filter(availableLocale -> Objects.equals(locale, availableLocale.getId()))
                .findFirst()
                .map(matchingLocale -> ValidationResult.EMPTY)
                .orElseGet(() -> ValidationResult.singleMessage((new ValidationMessage(MISSING_LOCALE, locale))));
    }
}
