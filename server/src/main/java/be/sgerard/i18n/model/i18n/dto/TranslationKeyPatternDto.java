package be.sgerard.i18n.model.i18n.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Pattern of a translation key to search for.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "TranslationKeyPattern", description = "Pattern of a translation key to search for.")
@Getter
public class TranslationKeyPatternDto {

    @Schema(description = "Strategy to use to match keys.")
    private final KeyPatternStrategy strategy;

    @Schema(description = "Pattern associated to the strategy.")
    private final String pattern;

    @JsonCreator
    public TranslationKeyPatternDto(@JsonProperty("strategy") KeyPatternStrategy strategy,
                                    @JsonProperty("pattern") String pattern) {
        this.strategy = (strategy != null) ? strategy : KeyPatternStrategy.CONTAINS;
        this.pattern = pattern;
    }

    /**
     * Strategy of the key pattern matching.
     */
    public enum KeyPatternStrategy {

        EQUALS,

        STARTS_WITH,

        ENDS_WITH,

        CONTAINS

    }
}
