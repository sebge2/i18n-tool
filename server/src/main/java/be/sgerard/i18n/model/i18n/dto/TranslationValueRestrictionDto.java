package be.sgerard.i18n.model.i18n.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Optional;

/**
 * Restriction on translations to look for based on a translation value.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "TranslationValueRestriction", description = "Restriction on translations to look for based on a translation value")
@Getter
public class TranslationValueRestrictionDto {

    @Schema(description = "The translation value.", required = true)
    private final String translation;

    @Schema(description = "Strategy to use to match values.")
    private final ValuePatternStrategy strategy;

    @Schema(description = "The locale associated to the translation to look for.")
    private final String locale;

    @JsonCreator
    public TranslationValueRestrictionDto(@JsonProperty("translation") String translation,
                                          @JsonProperty("strategy") ValuePatternStrategy strategy,
                                          @JsonProperty("locale") String locale) {
        this.translation = translation;
        this.strategy = strategy;
        this.locale = locale;
    }

    /**
     * @see #locale
     */
    public Optional<String> getLocale() {
        return Optional.ofNullable(locale);
    }

    /**
     * Strategy of the value pattern matching.
     */
    public enum ValuePatternStrategy {

        EQUALS,

        STARTS_WITH,

        ENDS_WITH,

        CONTAINS

    }
}
