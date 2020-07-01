package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.i18n.BundleType;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Translation bundle file part of a workspace of a repository.
 *
 * @author Sebastien Gerard
 */
public class BundleFileEntity {

    @NotNull
    private String name;

    @NotNull
    private String location;

    @NotNull
    private BundleType type;

    @AccessType(AccessType.Type.PROPERTY)
    private final Set<BundleFileEntryEntity> files = new HashSet<>();

    @PersistenceConstructor
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
