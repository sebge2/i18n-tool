package be.sgerard.i18n.model.i18n.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Page of translations associated to a {@link be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity bundle key}.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "TranslationsPageRow", description = "Page in a list of paginated translations.")
@JsonDeserialize(builder = TranslationsPageRowDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class TranslationsPageRowDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "Unique id of this row which is the id of the bundle key", required = true)
    private final String id;

    @Schema(description = "Workspace id associated to this translation.", required = true)
    private final String workspace;

    @Schema(description = "Bundle id associated to this translation.", required = true)
    private final String bundleFile;

    @Schema(description = "The id of the key for all the translations of this row", required = true)
    private final String bundleKeyId;

    @Schema(description = "The key for all the translations of this row", required = true)
    private final String bundleKey;

    @Schema(description = "All the translations of this row", required = true)
    private final List<TranslationsPageTranslationDto> translations;

    /**
     * Builder of a {@link TranslationsPageRowDto page row}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
