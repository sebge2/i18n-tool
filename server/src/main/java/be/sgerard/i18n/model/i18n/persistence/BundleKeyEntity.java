package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Translations of a certain key part of translation bundle.
 *
 * @author Sebastien Gerard
 */
@Document("bundle_key")
@Getter
@Setter
public class BundleKeyEntity {

    /**
     * The unique translation key id.
     */
    @Id
    private String id;

    /**
     * The associated {@link WorkspaceEntity workspace}.
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
    private String key;

    /**
     * Key used to sort all translation entities. It's composed of:
     * <ol>
     *     <li>the workspace,</li>
     *     <li>the bundle file,</li>
     *     <li>the bundle key</li>
     * </ol>
     */
    @NotNull
    @Indexed
    private String sortingKey;

    /**
     * All {@link BundleKeyTranslationEntity translations} mapped by their locale ids.
     */
    @NotNull
    @Singular
    private Map<String, BundleKeyTranslationEntity> translations = new HashMap<>();

    @PersistenceConstructor
    BundleKeyEntity() {
    }

    public BundleKeyEntity(String workspace,
                           String bundleFile,
                           String key) {
        this.id = UUID.randomUUID().toString();
        this.workspace = workspace;
        this.bundleFile = bundleFile;
        this.key = key;
        this.sortingKey = String.format("%s%s%s", workspace, bundleFile, key);
    }

    /**
     * Returns whether there is a translation for the specified locale.
     *
     * @see TranslationLocaleEntity#getId()
     */
    public boolean hasTranslations(String translationLocale) {
        return translations.containsKey(translationLocale);
    }

    /**
     * Returns the {@link BundleKeyTranslationEntity translation} for the specified locale.
     *
     * @see TranslationLocaleEntity#getId()
     */
    public Optional<BundleKeyTranslationEntity> getTranslation(String translationLocale) {
        return Optional
                .ofNullable(translations.get(translationLocale));
    }

    /**
     * Returns the {@link BundleKeyTranslationEntity translation} for the specified locale.
     *
     * @see TranslationLocaleEntity#getId()
     */
    public BundleKeyTranslationEntity getTranslationOrDie(String translationLocale) {
        return getTranslation(translationLocale)
                .orElseThrow(() -> new IllegalStateException("There is no translation for locale [" + translationLocale + "]"));
    }

    /**
     * Returns the {@link BundleKeyTranslationEntity translation} for the specified locale. If there is no such translation,
     * a new one is created.
     *
     * @see TranslationLocaleEntity#getId()
     */
    public BundleKeyTranslationEntity getTranslationOrCreate(String translationLocale) {
        return getTranslation(translationLocale)
                .orElseGet(() -> {
                    final BundleKeyTranslationEntity translation = new BundleKeyTranslationEntity(translationLocale, -1, null);

                    translations.put(translationLocale, translation);

                    return translation;
                });
    }

    /**
     * Adds a new translation to this bundle.
     */
    public BundleKeyEntity addTranslation(String locale, long index, String originalValue) {
        return addTranslation(new BundleKeyTranslationEntity(locale, index, originalValue));
    }

    /**
     * Adds a new translation to this bundle.
     */
    public BundleKeyEntity addTranslation(BundleKeyTranslationEntity translation) {
        this.translations.put(translation.getLocale(), translation);
        return this;
    }
}
