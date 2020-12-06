package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.support.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Optional;

/**
 * Translation of a certain {@link BundleKeyEntity key} part of translation bundle.
 *
 * @author Sebastien Gerard
 */
@Getter
@Setter
public class BundleKeyTranslationEntity {

    /**
     * The {@link TranslationLocaleEntity locale} of the translation.
     */
    @NotNull
    private String locale;

    /**
     * The unique index of this translation has defined in the original file.
     * <p>
     * If the index is negative, it means that this translation was not originally defined in the file.
     */
    @NotNull
    private Long index;

    /**
     * The original translation has found in the repository.
     */
    private String originalValue;

    /**
     * The entity containing modification applied on the translation.
     */
    private BundleKeyTranslationModificationEntity modification;

    @PersistenceConstructor
    BundleKeyTranslationEntity() {
    }

    public BundleKeyTranslationEntity(String locale,
                                      String originalValue,
                                      long index) {
        this.locale = locale;
        this.index = index;

        this.setOriginalValue(originalValue);
    }

    public BundleKeyTranslationEntity(String locale) {
        this(locale, null, Integer.MAX_VALUE);
    }

    /**
     * @see #originalValue
     */
    public Optional<String> getOriginalValue() {
        return Optional.ofNullable(originalValue);
    }

    /**
     * @see #originalValue
     */
    public BundleKeyTranslationEntity setOriginalValue(String originalValue) {
        final String modifiedOriginalValue = Optional.ofNullable(originalValue).filter(StringUtils::isNotEmptyString).orElse(null);
        final String currentUpdatedValue = this.getModification().flatMap(BundleKeyTranslationModificationEntity::getUpdatedValue).orElse(null);

        if (Objects.equals(modifiedOriginalValue, currentUpdatedValue)) {
            this.originalValue = modifiedOriginalValue;
            this.modification = null;
        } else {
            this.originalValue = modifiedOriginalValue;
        }

        return this;
    }

    /**
     * @see #modification
     */
    public Optional<BundleKeyTranslationModificationEntity> getModification() {
        return Optional.ofNullable(modification);
    }

    /**
     * @see #modification
     */
    public BundleKeyTranslationEntity setModification(BundleKeyTranslationModificationEntity modification) {
        if (modification == null) {
            this.modification = null;
            return this;
        }

        final String currentUpdatedValue = this.getModification().flatMap(BundleKeyTranslationModificationEntity::getUpdatedValue).orElse(null);
        final String newUpdatedValue = modification.getUpdatedValue().orElse(null);

        if (newUpdatedValue != null) {
            if (Objects.equals(newUpdatedValue, originalValue)) {
                this.modification = null;
            } else {
                if (!Objects.equals(currentUpdatedValue, newUpdatedValue)) {
                    this.modification = modification;
                } else {
                    // nothing to do, the update match the previous update
                }
            }
        } else {
            this.modification = null;
        }

        return this;
    }

    /**
     * Returns the translation value that will be used at the end.
     */
    public Optional<String> getValue() {
        return getModification()
                .flatMap(BundleKeyTranslationModificationEntity::getUpdatedValue)
                .or(this::getOriginalValue);
    }
}
