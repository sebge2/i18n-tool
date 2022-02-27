package be.sgerard.i18n.model.translator.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(name = "AzureTranslatorConfig", description = "Config of Azure Translator.")
@Getter
public class AzureTranslatorConfigDto {

    @Schema(description = "API key of your Google account.", required = true)
    private final String subscriptionKey;

    @Schema(description = "Region associated to the subscription.", required = true, example = "francecentral")
    private final String subscriptionRegion;

    @JsonCreator
    public AzureTranslatorConfigDto(@JsonProperty("subscriptionKey") String apiKey,
                                    @JsonProperty("subscriptionRegion") String subscriptionRegion) {
        this.subscriptionKey = apiKey;
        this.subscriptionRegion = subscriptionRegion;
    }
}
