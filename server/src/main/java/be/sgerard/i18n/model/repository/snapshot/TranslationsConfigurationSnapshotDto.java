package be.sgerard.i18n.model.repository.snapshot;

import be.sgerard.i18n.model.i18n.snapshot.BundleConfigurationSnapshotDto;
import be.sgerard.i18n.model.repository.persistence.TranslationsConfigurationEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

/**
 * Snapshot of a {@link TranslationsConfigurationEntity translations configuration}.
 *
 * @author Sebastien Gerard
 */
@JsonDeserialize(builder = TranslationsConfigurationSnapshotDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class TranslationsConfigurationSnapshotDto {

    /**
     * @see TranslationsConfigurationEntity#getBundles()
     */
    private final Collection<BundleConfigurationSnapshotDto> bundles;

    /**
     * @see TranslationsConfigurationEntity#getIgnoredKeys()
     */
    private final List<String> ignoredKeys;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
