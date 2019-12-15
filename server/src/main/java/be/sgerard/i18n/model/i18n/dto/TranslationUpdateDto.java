package be.sgerard.i18n.model.i18n.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

/**
 * Update of a particular translation.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "TranslationUpdate", description = "Update of a particular translation.")
@Getter
@Builder(builderClassName = "Builder")
@JsonDeserialize(builder = TranslationUpdateDto.Builder.class)
public class TranslationUpdateDto {

    @Schema(description = "Id of the bundleKey.", required = true)
    private final String bundleKeyId;

    @Schema(description = "Id of the translation locale.", required = true)
    private final String localeId;

    @Schema(description = "Value of the translation.")
    private final String translation;

    /**
     * @see #translation
     */
    public Optional<String> getTranslation() {
        return Optional.ofNullable(translation);
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

    }
}
