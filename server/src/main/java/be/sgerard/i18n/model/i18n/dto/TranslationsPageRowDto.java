package be.sgerard.i18n.model.i18n.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * Page of translations.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "TranslationsPage", description = "List of paginated translations.")
@JsonDeserialize(builder = TranslationsPageRowDto.Builder.class)
@Getter
public class TranslationsPageRowDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "The key for all the translations of this row", required = true)
    private final String bundleKey;

    @Schema(description = "All the translations of this row", required = true)
    private final List<BundleKeyTranslationDto> translations;

    private TranslationsPageRowDto(Builder builder) {
        bundleKey = builder.bundleKey;
        translations = unmodifiableList(builder.translations);
    }

    /**
     * Builder of {@link TranslationsPageRowDto bundle keys page}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private String bundleKey;
        private final List<BundleKeyTranslationDto> translations = new ArrayList<>();

        private Builder() {
        }

        public Builder bundleKey(String bundleKey) {
            this.bundleKey = bundleKey;
            return this;
        }

        public Builder translations(List<BundleKeyTranslationDto> translations) {
            this.translations.addAll(translations);
            return this;
        }

        public TranslationsPageRowDto build() {
            return new TranslationsPageRowDto(this);
        }
    }
}
