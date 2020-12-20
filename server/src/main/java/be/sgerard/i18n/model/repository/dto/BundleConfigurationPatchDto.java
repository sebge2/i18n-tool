package be.sgerard.i18n.model.repository.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

/**
 * Patch for modifying configurations of bundles of a repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "BundleConfigurationPatch", description = "Configurations of bundles of a repository.")
@JsonDeserialize(builder = BundleConfigurationPatchDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class BundleConfigurationPatchDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "All the paths that are included when scanning bundles of this type.")
    private final List<String> includedPaths;

    @Schema(description = "All the paths that are ignored when scanning bundles of this type.")
    private final List<String> ignoredPaths;

    /**
     * @see #includedPaths
     */
    public Optional<List<String>> getIncludedPaths() {
        return Optional.ofNullable(includedPaths);
    }

    /**
     * @see #ignoredPaths
     */
    public Optional<List<String>> getIgnoredPaths() {
        return Optional.ofNullable(ignoredPaths);
    }

    /**
     * Builder of {@link BundleConfigurationPatchDto configuration patch}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
