package be.sgerard.i18n.service.locale.validation;

import be.sgerard.i18n.model.locale.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.locale.TranslationLocaleManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity.toUserString;

/**
 * {@link TranslationLocaleValidator Validator} checking that the locale is unique (region-language-variants).
 *
 * @author Sebastien Gerard
 */
@Component
public class TranslationLocaleUniqueValidator implements TranslationLocaleValidator {

    /**
     * Validation message key specifying that there is already such a locale.
     */
    public static final String DUPLICATED_LOCALE = "validation.locale.duplicated";

    private final TranslationLocaleManager localeManager;

    public TranslationLocaleUniqueValidator(TranslationLocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    @Override
    public Mono<ValidationResult> beforePersist(TranslationLocaleEntity locale) {
        return localeManager
                .findAll()
                .filter(existingTranslationLocale -> existingTranslationLocale.matchLocale(locale))
                .count()
                .map(count -> {
                    final ValidationResult.Builder builder = ValidationResult.builder();

                    if (count > 0) {
                        builder.messages(
                                new ValidationMessage(DUPLICATED_LOCALE, toUserString(locale.getLanguage(), locale.getRegion().orElse(null), locale.getVariants()))
                        );
                    }

                    return builder.build();
                });
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(TranslationLocaleEntity original, TranslationLocaleDto update) {
        return localeManager
                .findAll()
                .filter(existingTranslationLocale -> !Objects.equals(update.getId(), existingTranslationLocale.getId()))
                .filter(existingTranslationLocale -> existingTranslationLocale.matchLocale(update))
                .count()
                .map(count -> {
                    final ValidationResult.Builder builder = ValidationResult.builder();

                    if (count > 0) {
                        builder.messages(
                                new ValidationMessage(DUPLICATED_LOCALE, toUserString(update.getLanguage(), update.getRegion().orElse(null), update.getVariants()))
                        );
                    }

                    return builder.build();
                });
    }
}
