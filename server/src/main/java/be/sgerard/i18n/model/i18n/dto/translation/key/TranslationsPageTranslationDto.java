package be.sgerard.i18n.model.i18n.dto.translation.key;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationModificationEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
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
@Builder(builderClassName = "Builder")
public class TranslationsPageTranslationDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(BundleKeyTranslationEntity translation) {
        return builder()
                .originalValue(translation.getOriginalValue().orElse(null))
                .updatedValue(translation.getModification().flatMap(BundleKeyTranslationModificationEntity::getUpdatedValue).orElse(null))
                .lastEditor(translation.getModification().flatMap(BundleKeyTranslationModificationEntity::getLastEditor).orElse(null));
    }

    @Schema(description = "The original value found when scanning bundle files (initializing step).", required = true)
    private final String originalValue;

    @Schema(description = "Value set by the end-user when editing translations.")
    private final String updatedValue;

    @Schema(description = "The username of the end-user that has edited this translation.")
    private final String lastEditor;

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
    }
}
