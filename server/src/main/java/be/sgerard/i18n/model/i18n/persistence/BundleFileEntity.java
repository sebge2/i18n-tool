package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileLocation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

/**
 * Translation bundle file part of a workspace of a repository.
 *
 * @author Sebastien Gerard
 */
@Getter
@Setter
public class BundleFileEntity {

    /**
     * The unique id of this bundle.
     */
    @Id
    private String id;

    /**
     * The bundle name.
     */
    @NotNull
    private String name;

    /**
     * The path location of this bundle in the repository.
     */
    @NotNull
    private String location;

    /**
     * The {@link BundleType bundle type}.
     */
    @NotNull
    private BundleType type;

    /**
     * All the file paths of this bundle.
     */
    @AccessType(AccessType.Type.PROPERTY)
    private Set<BundleFileEntryEntity> files = new HashSet<>();

    @PersistenceConstructor
    BundleFileEntity() {
    }

    public BundleFileEntity(String name,
                            String location,
                            BundleType type,
                            Collection<BundleFileEntryEntity> files) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.location = location;
        this.type = type;
        this.files.addAll(files);
    }

    public BundleFileEntity(ScannedBundleFile bundleFile) {
        this(
                bundleFile.getName(),
                bundleFile.getLocationDirectory().toString(),
                bundleFile.getType(),
                bundleFile.getFiles().stream().map(BundleFileEntryEntity::new).collect(toList())
        );
    }

    /**
     * Returns the {@link ScannedBundleFileLocation location} of the scanned bundle file.
     */
    public ScannedBundleFileLocation toLocation() {
        return new ScannedBundleFileLocation(new File(getLocation()), getName());
    }
}
