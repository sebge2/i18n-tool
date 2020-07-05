package be.sgerard.i18n.model.i18n.dto;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Optional;

/**
 * Translation in a {@link TranslationsPageRowDto translation page row}.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "TranslationsPageTranslation", description = "Translation in a translation page row.")
@JsonDeserialize(builder = TranslationsPageTranslationDto.Builder.class)
@Getter
public class TranslationsPageTranslationDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(BundleKeyTranslationEntity translation) {
        return builder()
                .id(translation.getId())
                .originalValue(translation.getOriginalValue().orElse(null))
                .updatedValue(translation.getUpdatedValue().orElse(null))
                .lastEditor(translation.getLastEditor().orElse(null));
    }

    @Schema(description = "Unique identifier of a translation.", required = true)
    private final String id;

    @Schema(description = "The original value found when scanning bundle files (initializing step).", required = true)
    private final String originalValue;

    @Schema(description = "Value set by the end-user when editing translations.")
    private final String updatedValue;

    @Schema(description = "The username of the end-user that has edited this translation.")
    private final String lastEditor;

    private TranslationsPageTranslationDto(Builder builder) {
        id = builder.id;
        originalValue = builder.originalValue;
        updatedValue = builder.updatedValue;
        lastEditor = builder.lastEditor;
    }

    /**
     * Returns the original translation.
     */
    public Optional<String> getOriginalValue() {
        return Optional.ofNullable(originalValue);
    }

    /**
     * Returns the updated translation (if it was edited).
     */
    public Optional<String> getUpdatedValue() {
        return Optional.ofNullable(updatedValue);
    }

    /**
     * Returns the id of the user that edited this translation.
     */
    public Optional<String> getLastEditor() {
        return Optional.ofNullable(lastEditor);
    }

    /**
     * Builder of a {@link TranslationsPageTranslationDto page translation}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String id;
        private String originalValue;
        private String updatedValue;
        private String lastEditor;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder originalValue(String originalValue) {
            this.originalValue = originalValue;
            return this;
        }

        public Builder updatedValue(String updatedValue) {
            this.updatedValue = updatedValue;
            return this;
        }

        public Builder lastEditor(String lastEditor) {
            this.lastEditor = lastEditor;
            return this;
        }

        public TranslationsPageTranslationDto build() {
            return new TranslationsPageTranslationDto(this);
        }
    }
}
