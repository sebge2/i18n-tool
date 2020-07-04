package be.sgerard.i18n.model.i18n.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Schema(description = "Last key defined in this page. It can be used to call the next page.", required = true, type = "java.lang.String")
    private final String lastKey;

    @Schema(description = "Rows where every row is associated to a bundle key", required = true)
    private final List<TranslationsPageRowDto> rows;

    private TranslationsPageDto(Builder builder) {
        lastKey = builder.lastKey;
        rows = unmodifiableList(builder.rows);
    }

    /**
     * Returns the last translation key of this page.
     *
     * @see TranslationsSearchRequestDto#getLastKey()
     */
    public Optional<String> getLastKey() {
        return Optional.ofNullable(lastKey);
    }

    /**
     * Builder of {@link TranslationsPageDto bundle keys page}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private final List<TranslationsPageRowDto> rows = new ArrayList<>();
        private String lastKey;

        private Builder() {
        }

        public Builder lastKey(String lastKey) {
            this.lastKey = lastKey;
            return this;
        }

        public Builder rows(List<TranslationsPageRowDto> rows) {
            this.rows.addAll(rows);
            return this;
        }

        public TranslationsPageDto build() {
            return new TranslationsPageDto(this);
        }
    }
}
