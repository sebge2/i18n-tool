package be.sgerard.i18n.model.i18n.dto.translate;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(name = "ExternalTranslationSource", description = "External translation source. This source can be for instance Google Translate.")
@Getter
public class ExternalTranslationSourceDto {

    /**
     * Returns the {@link ExternalTranslationSourceDto DTO} for the specified {@link ExternalTranslatorConfigEntity configuration}.
     */
    public static ExternalTranslationSourceDto fromConfig(ExternalTranslatorConfigEntity config) {
        return new ExternalTranslationSourceDto(config.getId(), config.getLabel(), config.getLinkUrl());
    }

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
