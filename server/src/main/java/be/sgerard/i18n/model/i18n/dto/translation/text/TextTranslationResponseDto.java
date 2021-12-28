package be.sgerard.i18n.model.i18n.dto.translation.text;

import be.sgerard.i18n.model.i18n.dto.translate.ExternalTranslationSourceDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

/**
 * Translations of a text.
 */
@Schema(name = "TextTranslationResponse", description = "Translations of a text. This text may have been translated by different sources.")
@Getter
public class TextTranslationResponseDto {

    @Schema(description = "Definition of external translation sources.")
    private final Collection<ExternalTranslationSourceDto> externalSources;

    @Schema(description = "Available translations.")
    private final List<TextTranslationDto> translations;

    @JsonCreator
    public TextTranslationResponseDto(@JsonProperty("externalSources") Collection<ExternalTranslationSourceDto> externalSources,
                                      @JsonProperty("translations") List<TextTranslationDto> translations) {
        this.externalSources = externalSources;

        this.translations = translations;
    }
}
