package be.sgerard.i18n.model.i18n.snapshot;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

/**
 * Snapshot of a {@link BundleKeyTranslationEntity bundle key translation}.
 *
 * @author Sebastien Gerard
 */
@JsonDeserialize(builder = BundleKeyTranslationSnapshotDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class BundleKeyTranslationSnapshotDto {

    /**
     * @see BundleKeyTranslationEntity#getLocale()
     */
    private final String locale;

    /**
     * @see BundleKeyTranslationEntity#getIndex()
     */
    private final Long index;

    /**
     * @see BundleKeyTranslationEntity#getOriginalValue()
     */
    private final String originalValue;

    /**
     * @see BundleKeyTranslationEntity#getModification()
     */
    private final BundleKeyTranslationModificationSnapshotDto modification;

    /**
     * @see #originalValue
     */
    public Optional<String> getOriginalValue() {
        return Optional.ofNullable(originalValue);
    }

    /**
     * @see #modification
     */
    public Optional<BundleKeyTranslationModificationSnapshotDto> getModification() {
        return Optional.ofNullable(modification);
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
