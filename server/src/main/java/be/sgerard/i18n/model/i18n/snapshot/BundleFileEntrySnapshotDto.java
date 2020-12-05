package be.sgerard.i18n.model.i18n.snapshot;

import be.sgerard.i18n.model.i18n.persistence.BundleFileEntryEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

/**
 * Dto for storing a {@link BundleFileEntryEntity bundle file entry} in a snapshot.
 *
 * @author Sebastien Gerard
 */
@JsonDeserialize(builder = BundleFileEntrySnapshotDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class BundleFileEntrySnapshotDto {

    /**
     * @see BundleFileEntryEntity#getId()
     */
    private final String id;

    /**
     * @see BundleFileEntryEntity#getLocale()
     */
    private final String locale;

    /**
     * @see BundleFileEntryEntity#getFile()
     */
    private final String file;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
