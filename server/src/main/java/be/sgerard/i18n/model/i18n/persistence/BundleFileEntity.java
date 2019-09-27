package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.i18n.BundleType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

import static java.util.Collections.unmodifiableList;

/**
 * @author Sebastien Gerard
 */
@Entity(name = "bundle_file")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"workspace", "location"})
        }
)
public class BundleFileEntity {

    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "workspace")
    private WorkspaceEntity workspace;

    @NotNull
    @Column(nullable = false, length = 1000)
    private String name;

    @NotNull
    @Column(nullable = false, columnDefinition = "TEXT")
    private String location;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BundleType type;

    @ElementCollection
    private Set<String> files = new HashSet<>();

    @ElementCollection
    private Set<Locale> locales = new HashSet<>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderColumn(name="key_index")
    private List<BundleKeyEntity> keys = new ArrayList<>();

    @Version
    private int version;

    BundleFileEntity() {
    }

    public BundleFileEntity(WorkspaceEntity workspace,
                            String name,
                            String location,
                            BundleType type,
                            Collection<Locale> locales,
                            Collection<String> files) {
        this.id = UUID.randomUUID().toString();

        this.workspace = workspace;
        this.workspace.addFile(this);

        this.name = name;
        this.location = location;
        this.type = type;

        this.locales.addAll(locales);
        this.files.addAll(files);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WorkspaceEntity getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceEntity workspace) {
        this.workspace = workspace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<BundleKeyEntity> getKeys() {
        return unmodifiableList(keys);
    }

    void addKey(BundleKeyEntity keyEntity) {
        this.keys.add(keyEntity);
    }

    public BundleType getType() {
        return type;
    }

    public void setType(BundleType type) {
        this.type = type;
    }

    public Set<String> getFiles() {
        return files;
    }

    public Set<Locale> getLocales() {
        return locales;
    }

    public void setLocales(Set<Locale> locales) {
        this.locales = locales;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
