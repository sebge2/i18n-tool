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

    @AccessType(AccessType.Type.PROPERTY)
    private final Set<BundleFileEntryEntity> files = new HashSet<>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderColumn(name = "key_index")
    private final List<BundleKeyEntity> keys = new ArrayList<>();

    @Version
    private int version;

    BundleFileEntity() {
    }

    public BundleFileEntity(String name,
                            String location,
                            BundleType type,
                            Collection<BundleFileEntryEntity> files) {
        this.name = name;
        this.location = location;
        this.type = type;
        this.files.addAll(files);
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
    public Set<BundleFileEntryEntity> getFiles() {
        return files;
    }

    /**
     * Sets all the file paths of this bundle.
     */
    public void setFiles(Set<BundleFileEntryEntity> files) {
        this.files.addAll(files);
    }
}
