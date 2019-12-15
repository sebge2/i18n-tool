package be.sgerard.i18n.model.i18n.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Optional;

/**
 * DTO asking the creation of a {@link TranslationLocaleDto translation locale}.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "TranslationLocaleCreation", description = "Description of a new locale associated to a translation (https://tools.ietf.org/html/bcp47)")
@JsonDeserialize(builder = TranslationLocaleCreationDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class TranslationLocaleCreationDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "The language.", required = true)
    private final String language;

    @Schema(description = "The region of the language.")
    private final String region;

    @Schema(description = "The variants in the region.")
    @Singular
    private final List<String> variants;

    @Schema(description = "The user friendly name for this locale.")
    private final String displayName;

    @Schema(description = "Icon to be displayed for this locale (library flag-icon-css).", required = true)
    private final String icon;

    /**
     * @see #region
     */
    public Optional<String> getRegion() {
        return Optional.ofNullable(region);
    }

    /**
     * @see #displayName
     */
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
