package be.sgerard.i18n.model.repository.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Configuration of how translations are retrieved and manage for a particular repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "TranslationsConfiguration", description = "Configuration of how translations are retrieved and manage for a particular repository.")
@JsonDeserialize(builder = TranslationsConfigurationDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class TranslationsConfigurationDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "The configurations of JSON bundles", required = true)
    private final BundleConfigurationDto jsonIcu;

    @Schema(description = "The configurations of Java Properties bundles", required = true)
    private final BundleConfigurationDto javaProperties;

    @Schema(description = "The fully-qualified names of keys to ignore.", required = true)
    private final List<String> ignoredKeys;

    /**
     * Builder of {@link TranslationsConfigurationDto translations configuration}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
