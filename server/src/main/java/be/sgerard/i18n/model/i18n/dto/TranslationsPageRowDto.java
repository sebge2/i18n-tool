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
@Schema(name = "TranslationsPageRow", description = "Page in a list of paginated translations.")
@JsonDeserialize(builder = TranslationsPageRowDto.Builder.class)
@Getter
public class TranslationsPageRowDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "Workspace id associated to this translation.", required = true)
    private final String workspace;

    @Schema(description = "Bundle id associated to this translation.", required = true)
    private final String bundleFile;

    @Schema(description = "The key for all the translations of this row", required = true)
    private final String bundleKey;

    @Schema(description = "All the translations of this row", required = true)
    private final List<TranslationsPageTranslationDto> translations;

    private TranslationsPageRowDto(Builder builder) {
        workspace = builder.workspace;
        bundleFile = builder.bundleFile;
        bundleKey = builder.bundleKey;
        translations = unmodifiableList(builder.translations);
    }

    /**
     * Builder of a {@link TranslationsPageRowDto page row}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private String workspace;
        private String bundleFile;
        private String bundleKey;
        private final List<TranslationsPageTranslationDto> translations = new ArrayList<>();

        private Builder() {
        }

        public Builder workspace(String workspace) {
            this.workspace = workspace;
            return this;
        }

        public Builder bundleFile(String bundleFile) {
            this.bundleFile = bundleFile;
            return this;
        }

        public Builder bundleKey(String bundleKey) {
            this.bundleKey = bundleKey;
            return this;
        }

        public Builder translations(List<TranslationsPageTranslationDto> translations) {
            this.translations.addAll(translations);
            return this;
        }

        public TranslationsPageRowDto build() {
            return new TranslationsPageRowDto(this);
        }
    }
}
