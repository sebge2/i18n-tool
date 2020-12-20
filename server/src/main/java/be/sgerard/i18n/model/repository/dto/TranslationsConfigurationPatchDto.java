package be.sgerard.i18n.model.repository.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.*;

/**
 * Configuration of how translations are retrieved and manage for a particular repository.
 *
 * @author Sébastien Gérard
 */
@Schema(name = "TranslationsConfigurationPatch", description = "Configuration of how translations are retrieved and manage for a particular repository")
@JsonDeserialize(builder = TranslationsConfigurationPatchDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class TranslationsConfigurationPatchDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "The configurations of JSON bundles")
    private final BundleConfigurationPatchDto jsonIcu;

    @Schema(description = "The configurations of Java Properties bundles")
    private final BundleConfigurationPatchDto javaProperties;

    @Schema(description = "The fully-qualified names of keys to ignore.")
    private final List<String> ignoredKeys;

    /**
     * @see #jsonIcu
     */
    public Optional<BundleConfigurationPatchDto> getJsonIcu() {
        return Optional.ofNullable(jsonIcu);
    }

    /**
     * @see #javaProperties
     */
    public Optional<BundleConfigurationPatchDto> getJavaProperties() {
        return Optional.ofNullable(javaProperties);
    }

    /**
     * @see #ignoredKeys
     */
    public Optional<List<String>> getIgnoredKeys() {
        return Optional.ofNullable(ignoredKeys);
    }

    /**
     * Builder of {@link TranslationsConfigurationPatchDto configuration patch}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
