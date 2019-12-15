package be.sgerard.i18n.model.i18n.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

/**
 * Page of translations.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "TranslationsPage", description = "List of paginated translations.")
@JsonDeserialize(builder = TranslationsPageDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class TranslationsPageDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "Rows where every row is associated to a bundle key", required = true)
    private final List<TranslationsPageRowDto> rows;

    @Schema(description = "All the ordered locales (i.e., columns order)", required = true)
    private final List<String> locales;

    /**
     * @see TranslationsSearchRequestDto#getLastPageKey()
     */
    @Schema(description = "The last element of the current page", required = false)
    private final String lastPageKey;

    /**
     * @see #lastPageKey
     */
    public Optional<String> getLastPageKey() {
        return Optional.ofNullable(lastPageKey);
    }

    /**
     * Builder of {@link TranslationsPageDto bundle keys page}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
