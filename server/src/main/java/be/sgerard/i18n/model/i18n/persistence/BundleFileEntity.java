package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

import static java.util.Collections.unmodifiableList;

/**
 * Translation bundle file part of a workspace of a repository.
 *
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
    private final Set<String> files = new HashSet<>();

    @ElementCollection
    private Set<Locale> locales = new HashSet<>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderColumn(name = "key_index")
    private final List<BundleKeyEntity> keys = new ArrayList<>();

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

    /**
     * Returns the unique id of this bundle file.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique id of this bundle file.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the associated {@link WorkspaceEntity workspace}.
     */
    public WorkspaceEntity getWorkspace() {
        return workspace;
    }

    /**
     * Sets the associated {@link WorkspaceEntity workspace}.
     */
    public void setWorkspace(WorkspaceEntity workspace) {
        this.workspace = workspace;
    }

    /**
     * Returns the bundle name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the bundle name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the path location of this bundle in the repository.
     */
    public String getLocation() {
        return location;
    }
    /**
     * Sets the path location of this bundle in the repository.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns all the {@link BundleKeyEntity keys} composing this bundle.
     */
    public List<BundleKeyEntity> getKeys() {
        return unmodifiableList(keys);
    }

    /**
     * Add the specified {@link BundleKeyEntity key} to this bundle.
     */
    void addKey(BundleKeyEntity keyEntity) {
        this.keys.add(keyEntity);
    }

    /**
     * Returns the {@link BundleType bundle type}.
     */
    public BundleType getType() {
        return type;
    }

    /**
     * Sets the {@link BundleType bundle type}.
     */
    public void setType(BundleType type) {
        this.type = type;
    }

    /**
     * Returns all the file paths of this bundle.
     */
    public Set<String> getFiles() {
        return files;
    }

    /**
     * Returns all the {@link Locale locales} of this bundle.
     */
    public Set<Locale> getLocales() {
        return locales;
    }

    /**
     * Sets all the {@link Locale locales} of this bundle.
     */
    public void setLocales(Set<Locale> locales) {
        this.locales = locales;
    }
}
