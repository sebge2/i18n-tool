package be.sgerard.i18n.model.i18n.dto;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Sebastien Gerard
 */
@ApiModel(description = "Translation of a key of a bundle file and associated to a locale.")
public class BundleKeyTranslationDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(BundleKeyTranslationEntity entity) {
        return builder()
                .id(entity.getId())
                .locale(entity.getLocale())
                .originalValue(entity.getOriginalValue().orElse(null))
                .updatedValue(entity.getUpdatedValue().orElse(null))
                .lastEditor(entity.getLastEditor().orElse(null));
    }

    @ApiModelProperty(notes = "Unique identifier of a translation.", required = true)
    private final String id;

    @ApiModelProperty(notes = "Locale associated to this translation.", required = true)
    private final String locale;

    @ApiModelProperty(notes = "The original value found when scanning bundle files (initializing step).", required = true)
    private final String originalValue;

    @ApiModelProperty(notes = "Value set by the end-user when editing translations.", required = true)
    private final String updatedValue;

    @ApiModelProperty(notes = "The username of the end-user that has edited this translation.", required = true)
    private final String lastEditor;

    private BundleKeyTranslationDto(Builder builder) {
        id = builder.id;
        locale = builder.locale;
        originalValue = builder.originalValue;
        updatedValue = builder.updatedValue;
        lastEditor = builder.lastEditor;
    }

    public String getId() {
        return id;
    }

    public String getLocale() {
        return locale;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getUpdatedValue() {
        return updatedValue;
    }

    public String getLastEditor() {
        return lastEditor;
    }

    public static final class Builder {
        private String id;
        private String locale;
        private String originalValue;
        private String updatedValue;
        private String lastEditor;

        private Builder() {
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder locale(String val) {
            locale = val;
            return this;
        }

        public Builder originalValue(String val) {
            originalValue = val;
            return this;
        }

        public Builder updatedValue(String val) {
            updatedValue = val;
            return this;
        }

        public Builder lastEditor(String val) {
            lastEditor = val;
            return this;
        }

        public BundleKeyTranslationDto build() {
            return new BundleKeyTranslationDto(this);
        }
    }
}
