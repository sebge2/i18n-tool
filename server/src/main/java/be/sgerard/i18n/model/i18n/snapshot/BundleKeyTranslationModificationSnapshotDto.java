package be.sgerard.i18n.model.i18n.snapshot;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationModificationEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

/**
 * Snapshot of a {@link BundleKeyTranslationModificationEntity translation modification}.
 *
 * @author Sebastien Gerard
 */
@JsonDeserialize(builder = BundleKeyTranslationModificationSnapshotDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class BundleKeyTranslationModificationSnapshotDto {

    /**
     * @see BundleKeyTranslationModificationEntity#getUpdatedValue()
     */
    private final String updatedValue;

    /**
     * @see BundleKeyTranslationModificationEntity#getLastEditor()
     */
    private final String lastEditor;

    /**
     * @see #updatedValue
     */
    public Optional<String> getUpdatedValue() {
        return Optional.ofNullable(updatedValue);
    }

    /**
     * @see #lastEditor
     */
    public Optional<String> getLastEditor() {
        return Optional.ofNullable(lastEditor);
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
