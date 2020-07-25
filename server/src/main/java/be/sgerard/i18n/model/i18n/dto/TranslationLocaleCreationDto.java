package be.sgerard.i18n.model.i18n.dto;

import be.sgerard.i18n.support.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * DTO asking the creation of a {@link TranslationLocaleDto translation locale}.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "TranslationLocaleCreation", description = "Description of a new locale associated to a translation (https://tools.ietf.org/html/bcp47)")
@JsonDeserialize(builder = TranslationLocaleCreationDto.Builder.class)
public class TranslationLocaleCreationDto {

    public static Builder builder() {
        return new Builder();
    }

    private final String language;
    private final String region;
    private final List<String> variants;
    private final String displayName;
    private final String icon;

    private TranslationLocaleCreationDto(Builder builder) {
        language = builder.language;
        region = builder.region;
        variants = unmodifiableList(builder.variants);
        displayName = builder.displayName;
        icon = builder.icon;
    }

    /**
     * Returns the language of this locale.
     */
    @Schema(description = "The language.", required = true)
    public String getLanguage() {
        return language;
    }

    /**
     * Returns the region of the language.
     */
    @Schema(description = "The region of the language.")
    public Optional<String> getRegion() {
        return Optional.ofNullable(region);
    }

    /**
     * Returns the variants of the region.
     */
    @Schema(description = "The variants in the region.")
    public List<String> getVariants() {
        return variants;
    }

    /**
     * Returns the user friendly name for this locale.
     */
    @Schema(description = "The user friendly name for this locale.")
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    /**
     * Returns the icon of this locale.
     */
    @Schema(description = "Icon to be displayed for this locale (library flag-icon-css).", required = true)
    public String getIcon() {
        return icon;
    }

    /**
     * Builder of {@link TranslationLocaleCreationDto creation DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String language;
        private String region;
        private final List<String> variants = new ArrayList<>();
        private String displayName;
        private String icon;

        private Builder() {
        }

        public Builder language(String language) {
            this.language = StringUtils.isEmptyString(language);
            return this;
        }

        public Builder region(String region) {
            this.region = StringUtils.isEmptyString(region);
            return this;
        }

        @JsonProperty("variants")
        public Builder variants(Collection<String> variants) {
            if (variants == null) {
                return this;
            }

            this.variants.addAll(variants);

            return this;
        }

        @JsonIgnore
        public Builder variants(String... variants) {
            if (variants == null) {
                return this;
            }

            this.variants.addAll(asList(variants));

            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = StringUtils.isEmptyString(displayName);
            return this;
        }

        public Builder icon(String icon) {
            this.icon = StringUtils.isEmptyString(icon);
            return this;
        }

        public TranslationLocaleCreationDto build() {
            return new TranslationLocaleCreationDto(this);
        }
    }
}
