package be.sgerard.i18n.model.i18n.dto.translation.text;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(name = "TextTranslation", description = "Translation of a text that may have translated from an external source.")
@Getter
public class TextTranslationDto {

    @Schema(description = "Unique id of the external source that translated.")
    private final String externalSource;

    @Schema(description = "Text before translation.", required = true)
    private final String originalText;

    @Schema(description = "Translation.", required = true)
    private final String text;

    @JsonCreator
    public TextTranslationDto(@JsonProperty("externalSource") String externalSource,
                              @JsonProperty("originalText") String originalText,
                              @JsonProperty("text") String text) {
        this.externalSource = externalSource;
        this.originalText = originalText;
        this.text = text;
    }
}
