package be.sgerard.i18n.model.translator.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(name = "GoogleTranslatorConfig", description = "Config of Google Translator.")
@Getter
public class GoogleTranslatorConfigDto {

    @Schema(description = "API key of your Google account.", required = true)
    private final String apiKey;

    @JsonCreator
    public GoogleTranslatorConfigDto(@JsonProperty("apiKey") String apiKey) {
        this.apiKey = apiKey;
    }
}
