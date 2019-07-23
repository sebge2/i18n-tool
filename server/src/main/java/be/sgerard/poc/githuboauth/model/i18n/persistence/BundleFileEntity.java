package be.sgerard.poc.githuboauth.model.i18n.persistence;

import be.sgerard.poc.githuboauth.model.i18n.BundleType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

import static java.util.Collections.unmodifiableCollection;

/**
 * @author Sebastien Gerard
 */
@Entity(name = "translation_bundle_file")
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
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private String location;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BundleType type;

    @ElementCollection
    private Set<String> files = new HashSet<>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private Collection<BundleKeyEntity> keys = new HashSet<>();

    @Version
    private int version;

    BundleFileEntity() {
    }

    public BundleFileEntity(WorkspaceEntity workspace,
                            String name,
                            String location,
                            BundleType type,
                            List<String> files) {
        this.id = UUID.randomUUID().toString();

        this.workspace = workspace;
        this.workspace.addFile(this);

        this.name = name;
        this.location = location;
        this.type = type;
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

    public Collection<BundleKeyEntity> getKeys() {
        return unmodifiableCollection(keys);
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
