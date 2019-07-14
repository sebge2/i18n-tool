package be.sgerard.poc.githuboauth.model.i18n.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import static java.util.Collections.unmodifiableCollection;

/**
 * @author Sebastien Gerard
 */
@Entity(name = "translation_bundle_key")
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
    @Column(nullable = false)
    private String key;

    @ManyToOne
    @JoinColumn(name = "bundle_file")
    private BundleFileEntity bundleFile;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private Collection<BundleKeyEntryEntity> entries = new HashSet<>();

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public BundleFileEntity getBundleFile() {
        return bundleFile;
    }

    public void setBundleFile(BundleFileEntity bundleFile) {
        this.bundleFile = bundleFile;
    }

    public Collection<BundleKeyEntryEntity> getEntries() {
        return unmodifiableCollection(entries);
    }

    void addEntry(BundleKeyEntryEntity entry) {
        this.entries.add(entry);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
