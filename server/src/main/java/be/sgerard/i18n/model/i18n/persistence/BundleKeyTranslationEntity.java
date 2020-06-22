package be.sgerard.i18n.model.i18n.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * Translation of a certain key part of translation bundle.
 *
 * @author Sebastien Gerard
 */
@NamedEntityGraph(
        name = BundleKeyTranslationEntity.GRAPH_FETCH_ENTRIES_TO_WORKSPACE,
        attributeNodes = {
                @NamedAttributeNode(value = "bundleKey", subgraph = "bundleKey-subgraph"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "bundleKey-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "bundleFile", subgraph = "workspace-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "workspace-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "workspace")
                        }
                )
        }
)
@Entity(name = "bundle_key_translation")
@Table(
        indexes = {@Index(columnList = "locale")},
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"bundle_key", "locale"})
        }
)
public class BundleKeyTranslationEntity {

    public static final String GRAPH_FETCH_ENTRIES_TO_WORKSPACE = "fetch-entry-with-bundles";

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "bundle_key")
    private BundleKeyEntity bundleKey;

    @NotNull
    @Column(nullable = false)
    private String locale;

    @Column(columnDefinition = "TEXT")
    private String originalValue;

    @Column(columnDefinition = "TEXT")
    private String updatedValue;

    @Column(length = 1000)
    private String lastEditor;

    @Version
    private int version;

    BundleKeyTranslationEntity() {
    }

    public BundleKeyTranslationEntity(BundleKeyEntity bundleKey,
                                      String locale,
                                      String originalValue) {
        this.id = UUID.randomUUID().toString();

        this.bundleKey = bundleKey;
        this.bundleKey.addTranslation(this);

        this.locale = locale;
        this.originalValue = originalValue;
    }

    /**
     * Returns the unique id of this translation.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique id of this translation.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the associated {@link BundleKeyEntity translation key}.
     */
    public BundleKeyEntity getBundleKey() {
        return bundleKey;
    }

    /**
     * Sets the associated {@link BundleKeyEntity translation key}.
     */
    public void setBundleKey(BundleKeyEntity bundleKey) {
        this.bundleKey = bundleKey;
    }

    /**
     * Returns the string representation of the locale of the translation.
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Returns the string representation of the locale of the translation.
     */
    public Locale getJavaLocale() {
        return Locale.forLanguageTag(getLocale());
    }

    /**
     * Sets the string representation of the locale of the translation.
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Returns the original translation.
     */
    public Optional<String> getOriginalValue() {
        return Optional.ofNullable(originalValue);
    }

    /**
     * Sets the original translation.
     */
    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    /**
     * Returns the updated translation (if it was edited).
     */
    public Optional<String> getUpdatedValue() {
        return Optional.ofNullable(updatedValue);
    }

    /**
     * Sets the updated translation (if it was edited).
     */
    public void setUpdatedValue(String updatedValue) {
        this.updatedValue = updatedValue;
    }

    /**
     * Returns the translation value that will be used at the end.
     */
    public Optional<String> getValue() {
        return getUpdatedValue()
                .or(this::getOriginalValue);
    }

    /**
     * Returns the id of the user that edited this translation.
     */
    public Optional<String> getLastEditor() {
        return Optional.ofNullable(lastEditor);
    }

    /**
     * Sets the id of the user that edited this translation.
     */
    public void setLastEditor(String lastEditor) {
        this.lastEditor = lastEditor;
    }
}
