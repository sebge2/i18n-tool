package be.sgerard.i18n.model.i18n.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Pattern of a translation key to search for.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "TranslationKeyPattern", description = "Pattern of a translation key to search for.")
public class TranslationKeyPatternDto {

    private final KeyPatternStrategy strategy;
    private final String pattern;

    @JsonCreator
    public TranslationKeyPatternDto(@JsonProperty("strategy") KeyPatternStrategy strategy,
                                    @JsonProperty("pattern") String pattern) {
        this.strategy = strategy;
        this.pattern = pattern;
    }

    public KeyPatternStrategy getStrategy() {
        return strategy;
    }

    public String getPattern() {
        return pattern;
    }

    /**
     * Strategy of the key pattern matching.
     */
    public enum KeyPatternStrategy {

        STARTS_WITH,

        ENDS_WITH,

        CONTAINS

    }
}
