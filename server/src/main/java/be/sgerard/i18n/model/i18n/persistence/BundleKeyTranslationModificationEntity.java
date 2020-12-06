package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.user.persistence.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.Optional;

/**
 * Modification applied on a {@link BundleKeyTranslationEntity trnaslation}.
 *
 * @author Sebastien Gerard
 */
@Getter
@Setter
public class BundleKeyTranslationModificationEntity {

    /**
     * The updated translation (if it was edited).
     */
    private String updatedValue;

    /**
     * The {@link UserEntity user} that edited this translation.
     * <p>
     * The editor may not exist anymore (if the user has been deleted in the mean time).
     */
    private String lastEditor;

    @PersistenceConstructor
    BundleKeyTranslationModificationEntity() {
    }

    public BundleKeyTranslationModificationEntity(String updatedValue, String lastEditor) {
        this.updatedValue = updatedValue;
        this.lastEditor = lastEditor;
    }

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
}
