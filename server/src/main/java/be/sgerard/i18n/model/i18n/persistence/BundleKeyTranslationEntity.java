package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

/**
 * Translation of a certain key part of translation bundle.
 *
 * @author Sebastien Gerard
 */
@Document("bundle_key_translation")
@Getter
@Setter
public class BundleKeyTranslationEntity {

    /**
     * The unique translation key id.
     */
    @Id
    private String id;

    /**
     * The associated {@link WorkspaceEntity workspace}. // TODO
     */
    @NotNull
    private String workspace;

    /**
     * The associated {@link BundleFileEntity bundle file}.
     */
    @NotNull
    private String bundleFile;

    /**
     * The associated translation key.
     */
    @NotNull
    private String bundleKey;

    /**
     * The {@link TranslationLocaleEntity locale} of the translation.
     */
    @NotNull
    private String locale; // TODO

    /**
     * The unique index of this translation has defined in the original file.
     *
     * If the index is negative, it means that this translation was not originally defined in the file.
     */
    @NotNull
    private Long index;

    /**
     * The original translation has found in the repository.
     */
    private String originalValue;

    /**
     * The updated translation (if it was edited).
     */
    private String updatedValue;

    /**
     * The {@link be.sgerard.i18n.model.security.user.persistence.UserEntity user} that edited this translation.
     */
    private String lastEditor; // TODO

    @PersistenceConstructor
    BundleKeyTranslationEntity() {
    }

    public BundleKeyTranslationEntity(String workspace,
                                      String bundleFile,
                                      String bundleKey,
                                      String locale,
                                      long index,
                                      String originalValue) {
        this.id = UUID.randomUUID().toString();
        this.workspace = workspace;
        this.bundleFile = bundleFile;
        this.bundleKey = bundleKey;
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
     * @see #updatedValue
     */
    public Optional<String> getUpdatedValue() {
        return Optional.ofNullable(updatedValue);
    }

    /**
     * Returns the translation value that will be used at the end.
     */
    public Optional<String> getValue() {
        return getUpdatedValue()
                .or(this::getOriginalValue);
    }

    /**
     * @see #lastEditor
     */
    public Optional<String> getLastEditor() {
        return Optional.ofNullable(lastEditor);
    }
}
