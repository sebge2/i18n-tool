package be.sgerard.i18n.model.i18n.dto.translation.text;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Request asking to translate a text.
 */
@Schema(name = "TextTranslationRequest", description = "Request asking to translate a text.")
@Getter
public class TextTranslationRequestDto {

    @Schema(description = "Text to translate.", required = true)
    private final String text;

    @Schema(description = "Id of the locale in which the text is written.", required = true)
    private final String fromLocaleId;

    @Schema(description = "Id of the locale in which the text must be translated.", required = true)
    private final String targetLocaleId;

    @JsonCreator
    public TextTranslationRequestDto(@JsonProperty("text") String text,
                                     @JsonProperty("fromLocaleId") String fromLocaleId,
                                     @JsonProperty("targetLocaleId") String targetLocaleId) {
        this.text = text;
        this.fromLocaleId = fromLocaleId;
        this.targetLocaleId = targetLocaleId;
    }
}
