package be.sgerard.i18n.model.i18n.snapshot;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Snapshot of a {@link BundleKeyEntity bundle key}.
 *
 * @author Sebastien Gerard
 */
@JsonDeserialize(builder = BundleKeySnapshotDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class BundleKeySnapshotDto {

    /**
     * @see BundleKeyEntity#getId()
     */
    private final String id;

    /**
     * @see BundleKeyEntity#getWorkspace()
     */
    private final String workspace;

    /**
     * @see BundleKeyEntity#getBundleFile()
     */
    private final String bundleFile;

    /**
     * @see BundleKeyEntity#getKey()
     */
    private final String key;

    /**
     * @see BundleKeyEntity#getSortingKey()
     */
    private final String sortingKey;

    /**
     * @see BundleKeyEntity#getTranslations()
     */
    private final Map<String, BundleKeyTranslationSnapshotDto> translations;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
