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
@JsonDeserialize(builder = TranslationsPageDto.Builder.class)
@Getter
public class TranslationsPageDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "Rows where every row is associated to a bundle key", required = true)
    private final List<TranslationsPageRowDto> rows;

    @Schema(description = "All the ordered locales (i.e., columns order)", required = true)
    private final List<String> locales;

    /**
     * @see TranslationsSearchRequestDto#getPageIndex()
     */
    @Schema(description = "The index of the page to look for (the first page has the index 0)", required = true)
    private final int pageIndex;

    private TranslationsPageDto(Builder builder) {
        pageIndex = builder.pageIndex;
        rows = unmodifiableList(builder.rows);
        locales = unmodifiableList(builder.locales);
    }

    /**
     * Builder of {@link TranslationsPageDto bundle keys page}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private final List<TranslationsPageRowDto> rows = new ArrayList<>();
        private final List<String> locales = new ArrayList<>();
        private int pageIndex = 0;

        private Builder() {
        }

        public Builder pageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
            return this;
        }

        public Builder rows(List<TranslationsPageRowDto> rows) {
            this.rows.addAll(rows);
            return this;
        }

        public Builder locales(List<String> locales) {
            this.locales.addAll(locales);
            return this;
        }

        public TranslationsPageDto build() {
            return new TranslationsPageDto(this);
        }
    }
}
