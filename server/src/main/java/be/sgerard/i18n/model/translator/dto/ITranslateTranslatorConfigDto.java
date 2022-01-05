package be.sgerard.i18n.model.translator.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(name = "ITranslateTranslatorConfig", description = "Config of iTranslate.com Translator.")
@Getter
public class ITranslateTranslatorConfigDto {

    @Schema(description = "Authorization Bearer token for your account.", required = true)
    private final String bearerToken;

    @JsonCreator
    public ITranslateTranslatorConfigDto(@JsonProperty("bearerToken") String bearerToken) {
        this.bearerToken = bearerToken;
    }
}
