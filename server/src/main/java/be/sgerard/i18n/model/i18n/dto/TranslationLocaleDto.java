package be.sgerard.i18n.model.i18n.dto;

import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * Description of a locale associated to a translation.
 *
 * @author Sebastien Gerard
 */
@ApiModel(value = "TranslationLocale", description = "Description of a locale associated to a translation (https://tools.ietf.org/html/bcp47)")
@JsonDeserialize(builder = TranslationLocaleDto.Builder.class)
public class TranslationLocaleDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(TranslationLocaleEntity entity) {
        return builder()
                .id(entity.getId())
                .language(entity.getLanguage())
                .region(entity.getRegion())
                .variants(entity.getVariants())
                .icon(entity.getIcon());
    }

    private final String id;
    private final String language;
    private final String region;
    private final List<String> variants;
    private final String icon;

    private TranslationLocaleDto(Builder builder) {
        id = builder.id;
        language = builder.language;
        region = builder.region;
        variants = unmodifiableList(builder.variants);
        icon = builder.icon;
    }

    /**
     * Returns the unique id of this locale.
     */
    @ApiModelProperty(notes = "The unique id of this locale", required = true)
    public String getId() {
        return id;
    }

    /**
     * Returns the language of this locale.
     */
    @ApiModelProperty(notes = "The language.", required = true)
    public String getLanguage() {
        return language;
    }

    /**
     * Returns the region of the language.
     */
    @ApiModelProperty(notes = "The region of the language.", required = true)
    public String getRegion() {
        return region;
    }

    /**
     * Returns the variants of the region.
     */
    @ApiModelProperty(notes = "The variants in the region.", required = true)
    public List<String> getVariants() {
        return variants;
    }

    /**
     * Returns the icon of this locale.
     */
    @ApiModelProperty(notes = "Icon to be displayed for this locale (library flag-icon-css).", required = true)
    public String getIcon() {
        return icon;
    }

    /**
     * Builder of {@link TranslationLocaleDto translation locale DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String id;
        private String language;
        private String region;
        private final List<String> variants = new ArrayList<>();
        private String icon;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        @JsonProperty("variants")
        public Builder variants(Collection<String> variants) {
            this.variants.addAll(variants);
            return this;
        }

        @JsonIgnore
        public Builder variants(String... variants) {
            return variants(asList(variants));
        }

        public Builder icon(String icon) {
            this.icon = icon;
            return this;
        }

        public TranslationLocaleDto build() {
            return new TranslationLocaleDto(this);
        }
    }
}
