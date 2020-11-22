package be.sgerard.i18n.model.i18n.dto;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Description of a locale associated to a translation.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "TranslationLocale", description = "Description of a locale associated to a translation (https://tools.ietf.org/html/bcp47)")
@JsonDeserialize(builder = TranslationLocaleDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class TranslationLocaleDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(TranslationLocaleEntity entity) {
        return builder()
                .id(entity.getId())
                .language(entity.getLanguage())
                .region(entity.getRegion().orElse(null))
                .variants(entity.getVariants())
                .displayName(entity.getDisplayName().orElse(null))
                .icon(entity.getIcon());
    }

    @Schema(description = "The unique id of this locale", required = true)
    private final String id;

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
     * @see #variants
     */
    public List<String> getVariants() {
        return variants;
    }

    /**
     * @see #displayName
     */
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    /**
     * Returns this DTO as a {@link Locale locale}.
     */
    public Locale toLocale() {
        return TranslationLocaleEntity.toLocale(getLanguage(), getRegion().orElse(null), getVariants());
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
