package be.sgerard.i18n.model.i18n.persistence;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.validation.constraints.NotNull;
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
                                      long index,
                                      String originalValue) {
        this.locale = locale;
        this.index = index;
        this.originalValue = originalValue;
    }

    /**
     * @see #originalValue
     */
    public Optional<String> getOriginalValue() {
        return Optional.ofNullable(originalValue);
    }

    /**
     * Returns {@link BundleKeyTranslationModificationEntity modification} applied to this translation.
     */
    public Optional<BundleKeyTranslationModificationEntity> getModification() {
        return Optional.ofNullable(modification);
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
