package be.sgerard.i18n.model.i18n.dto.snapshot;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Optional;

/**
 * Dto for storing a {@link TranslationLocaleEntity translation locale} in a snapshot.
 *
 * @author Sebastien Gerard
 */
@JsonDeserialize(builder = TranslationLocaleSnapshotDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class TranslationLocaleSnapshotDto {

    /**
     * @see TranslationLocaleEntity#getId()
     */
    private final String id;

    /**
     * @see TranslationLocaleEntity#getLanguage()
     */
    private final String language;

    /**
     * @see TranslationLocaleEntity#getRegion()
     */
    private final String region;

    /**
     * @see TranslationLocaleEntity#getVariants()
     */
    @Singular
    private final List<String> variants;

    /**
     * @see TranslationLocaleEntity#getDisplayName()
     */
    private final String displayName;

    /**
     * @see TranslationLocaleEntity#getIcon()
     */
    private final String icon;

    /**
     * @see #region
     */
    public Optional<String> getRegion() {
        return Optional.ofNullable(region);
    }

    /**
     * @see #variants
     */
    public List<String> getVariants() {
        return variants;
    }

    /**
     * @see #displayName
     */
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }

}
