package be.sgerard.i18n.service.locale.validation;

import be.sgerard.i18n.model.locale.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static org.springframework.util.StringUtils.isEmpty;


/**
 * {@link TranslationLocaleValidator Validator} checking that the following expectations:
 * <ul>
 *     <li>the language is mandatory,</li>
 *     <li>the icon is mandatory,</li>
 *     <li>the language and region must have a length of 2 characters.</li>
 * </ul>
 *
 * @author Sebastien Gerard
 */
@Component
public class GlobalTranslationLocaleValidator implements TranslationLocaleValidator {

    /**
     * Validation message key specifying that the language is missing.
     */
    public static final String MISSING_LANGUAGE = "validation.locale.missing-language";

    /**
     * Validation message key specifying that the icon is missing.
     */
    public static final String MISSING_ICON = "validation.locale.missing-icon";

    /**
     * Validation message key specifying that the language and region format is wrong.
     */
    public static final String WRONG_FORMAT = "validation.locale.wrong-format";

    public GlobalTranslationLocaleValidator() {
    }

    @Override
    public Mono<ValidationResult> beforePersist(TranslationLocaleEntity locale) {
        return doValidate(locale.getLanguage(), locale.getRegion().orElse(null), locale.getIcon());
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(TranslationLocaleEntity original, TranslationLocaleDto update) {
        return doValidate(update.getLanguage(), update.getRegion().orElse(null), update.getIcon());
    }

    /**
     * Validates the specified locale fields.
     */
    private Mono<ValidationResult> doValidate(String language, String region, String icon) {
        final ValidationResult.Builder builder = ValidationResult.builder();

        if (isEmpty(language)) {
            builder.messages(new ValidationMessage(MISSING_LANGUAGE));
        } else if (language.length() != 2) {
            builder.messages(new ValidationMessage(WRONG_FORMAT));
        }

        if (!isEmpty(region) && region.length() != 2) {
            builder.messages(new ValidationMessage(WRONG_FORMAT));
        }

        if (isEmpty(icon)) {
            builder.messages(new ValidationMessage(MISSING_ICON));
        }

        return Mono.just(builder.build());
    }

}
