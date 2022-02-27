package be.sgerard.i18n.model.translator.dto;

import be.sgerard.i18n.model.repository.dto.RepositoryDto;
import be.sgerard.i18n.service.translator.ExternalTranslatorConfigType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(name = "ExternalTranslatorConfig", description = "External (not from this tool) source of translation.")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExternalTranslatorGenericRestConfigDto.class, name = ExternalTranslatorGenericRestConfigDto.TYPE),
})
@Getter
public abstract class ExternalTranslatorConfigDto {

    @Schema(description = "The unique id of this source.", required = true)
    private final String id;

    @Schema(description = "Label associated to this source.", required = true)
    private final String label;

    @Schema(description = "Link URL describing this source.", required = true)
    private final String linkUrl;

    protected ExternalTranslatorConfigDto(BaseBuilder<?, ?> builder) {
        this.id = builder.id;
        this.label = builder.label;
        this.linkUrl = builder.linkUrl;
    }

    /**
     * Returns the {@link ExternalTranslatorConfigType configuration type}.
     */
    public abstract ExternalTranslatorConfigType getType();

    /**
     * Fills the builder with the specified DTO.
     */
    @SuppressWarnings("unchecked")
    protected static <B extends BaseBuilder<?, ?> > B fillBuilder(B builder, ExternalTranslatorConfigDto original){
        return (B) builder
                .id(original.getId())
                .label(original.getLabel())
                .linkUrl(original.getLinkUrl());
    }

    /**
     * Builder of {@link RepositoryDto repository DTO}.
     */
    public static abstract class BaseBuilder<C extends ExternalTranslatorConfigDto, B extends BaseBuilder<C, B>> {

        private String id;
        private String label;
        private String linkUrl;

        public B id(String id) {
            this.id = id;
            return self();
        }

        public B label(String label) {
            this.label = label;
            return self();
        }

        public B linkUrl(String linkUrl) {
            this.linkUrl = linkUrl;
            return self();
        }

        public abstract C build();

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }
    }
}
