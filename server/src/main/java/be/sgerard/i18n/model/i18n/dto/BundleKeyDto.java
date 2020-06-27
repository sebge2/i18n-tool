package be.sgerard.i18n.model.i18n.dto;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * Translation key part of a translation bundle.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "BundleKey", description = "Key in a bundle file associated to translations.")
@JsonDeserialize(builder = BundleKeyDto.Builder.class)
public class BundleKeyDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "Unique identifier of a key.", required = true)
    private final String id;

    @Schema(description = "Key associated to translation entries.", required = true)
    private final String key;

    @Schema(description = "All translations associated to this key.", required = true)
    private final List<BundleKeyTranslationDto> translations;

    private BundleKeyDto(Builder builder) {
        id = builder.id;
        key = builder.key;
        translations = unmodifiableList(builder.translations);
    }

    /**
     * Returns the unique id of this key.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the translation key as specified in bundle files.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns all the {@link BundleKeyTranslationEntity translations} of this key.
     */
    public List<BundleKeyTranslationDto> getTranslations() {
        return translations;
    }

    /**
     * Builder of {@link BundleKeyDto bundle key}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String id;
        private String key;
        private final List<BundleKeyTranslationDto> translations = new ArrayList<>();

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        @JsonProperty("translations")
        public Builder translations(List<BundleKeyTranslationDto> translations) {
            this.translations.addAll(translations);
            return this;
        }

        @JsonIgnore
        public Builder translations(BundleKeyTranslationDto... translations) {
            return translations(asList(translations));
        }

        public BundleKeyDto build() {
            return new BundleKeyDto(this);
        }
    }
}
