package be.sgerard.i18n.model.i18n.dto.translation.key;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Specification of the page to search for.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "TranslationsSearchPageSpec", description = "Specification of the page to search for.")
@Getter
@Builder(builderClassName = "Builder")
@JsonDeserialize(builder = TranslationsSearchPageSpecDto.Builder.class)
public class TranslationsSearchPageSpecDto {

    @Schema(description = "Flag indicating whether the request asks the next page, or the previous page.")
    private final boolean nextPage;

    @Schema(description = "If the search is for the next page, this represents the last element of the previous page, " +
            "otherwise it's the first element following page.")
    private final String keyOtherPage;

    /**
     * Builder of {@link TranslationsSearchPageSpecDto translations search page request}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }

}
