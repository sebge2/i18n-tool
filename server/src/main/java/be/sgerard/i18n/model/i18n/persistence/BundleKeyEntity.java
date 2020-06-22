package be.sgerard.i18n.model.i18n.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import static java.util.Collections.unmodifiableCollection;

/**
 * Translation key part of a translation bundle.
 *
 * @author Sebastien Gerard
 */
@Entity(name = "bundle_key")
@Table(
        indexes = {
                @Index(columnList = "key"),
                @Index(columnList = "bundle_file")
        }
)
public class BundleKeyEntity {

    @Id
    private String id;

    @NotNull
    @Column(nullable = false, length = 1000)
    private String key;

    @ManyToOne
    @JoinColumn(name = "bundle_file")
    private BundleFileEntity bundleFile;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private final Collection<BundleKeyTranslationEntity> translations = new HashSet<>();

    @Version
    private int version;

    BundleKeyEntity() {
    }

    public BundleKeyEntity(BundleFileEntity bundleFile, String key) {
        this.id = UUID.randomUUID().toString();

        this.bundleFile = bundleFile;
        this.bundleFile.addKey(this);

        this.key = key;
    }

    /**
     * Returns the unique id of this key.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique id of this key.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the translation key as specified in bundle files.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the translation key as specified in bundle files.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns the {@link BundleFileEntity bundle file} containing this key.
     */
    public BundleFileEntity getBundleFile() {
        return bundleFile;
    }

    /**
     * Sets the {@link BundleFileEntity bundle file} containing this key.
     */
    public void setBundleFile(BundleFileEntity bundleFile) {
        this.bundleFile = bundleFile;
    }

    /**
     * Returns all the {@link BundleKeyTranslationEntity translations} of this key.
     */
    public Collection<BundleKeyTranslationEntity> getTranslations() {
        return unmodifiableCollection(translations);
    }

    /**
     * Adds a {@link BundleKeyTranslationEntity translation} of this key.
     */
    void addTranslation(BundleKeyTranslationEntity entry) {
        this.translations.add(entry);
    }

}
