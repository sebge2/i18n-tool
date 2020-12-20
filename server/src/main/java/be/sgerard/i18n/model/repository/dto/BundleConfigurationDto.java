package be.sgerard.i18n.model.repository.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Configurations of bundles of a repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "BundleConfiguration", description = "Configurations of bundles of a repository.")
@JsonDeserialize(builder = BundleConfigurationDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class BundleConfigurationDto {

    @Schema(description = "All the paths that are included when scanning bundles of this type.", required = true)
    private final List<String> includedPaths;

    @Schema(description = "All the paths that are ignored when scanning bundles of this type.", required = true)
    private final List<String> ignoredPaths;

    /**
     * Builder of {@link BundleConfigurationDto bundle configuration}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
