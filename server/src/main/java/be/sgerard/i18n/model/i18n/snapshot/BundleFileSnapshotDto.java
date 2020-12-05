package be.sgerard.i18n.model.i18n.snapshot;

import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

/**
 * Dto for storing a {@link BundleFileEntity bundle file} in a snapshot.
 *
 * @author Sebastien Gerard
 */
@JsonDeserialize(builder = BundleFileSnapshotDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class BundleFileSnapshotDto {

    /**
     * @see BundleFileEntity#getId()
     */
    private final String id;

    /**
     * @see BundleFileEntity#getName()
     */
    private final String name;

    /**
     * @see BundleFileEntity#getLocation()
     */
    private final String location;

    /**
     * @see BundleFileEntity#getType()
     */
    private final BundleType type;

    /**
     * @see BundleFileEntity#getFiles()
     */
    private final Set<BundleFileEntrySnapshotDto> files;

    /**
     * @see BundleFileEntity#getNumberKeys()
     */
    private final long numberKeys;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
