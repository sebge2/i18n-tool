package be.sgerard.i18n.model.i18n.snapshot;

import be.sgerard.i18n.model.i18n.persistence.BundleConfigurationEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Snapshot of a {@link BundleConfigurationEntity bundle configuration}.
 *
 * @author Sebastien Gerard
 */
@JsonDeserialize(builder = BundleConfigurationSnapshotDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class BundleConfigurationSnapshotDto {

    /**
     * @see BundleConfigurationEntity#getBundleType()
     */
    private final BundleType bundleType;

    /**
     * @see BundleConfigurationEntity#getIncludedPaths()
     */
    private final List<String> includedPaths;

    /**
     * @see BundleConfigurationEntity#getIgnoredPaths()
     */
    private final List<String> ignoredPaths;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }

}
