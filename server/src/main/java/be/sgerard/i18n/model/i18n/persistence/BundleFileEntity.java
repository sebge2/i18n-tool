package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.i18n.BundleType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
}
