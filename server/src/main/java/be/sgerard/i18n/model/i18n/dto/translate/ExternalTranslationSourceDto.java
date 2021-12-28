package be.sgerard.i18n.model.i18n.dto.translate;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(name = "ExternalTranslationSource", description = "External translation source. This source can be for instance Google Translate.")
@Getter
public class ExternalTranslationSourceDto {

    @Schema(description = "Unique id of this source.", required = true)
    private final String id;

    @Schema(description = "Label of this source.", required = true)
    private final String label;

    @Schema(description = "URL pointing to the translation source.", required = true)
    private final String url;

    public ExternalTranslationSourceDto(@JsonProperty("id") String id,
                                        @JsonProperty("label") String label,
                                        @JsonProperty("url") String url) {
        this.id = id;
        this.label = label;
        this.url = url;
    }
}
