package be.sgerard.i18n.model.snapshot;

import be.sgerard.i18n.model.snapshot.file.SnapshotMetadataDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Snapshot freezing the state of the tool in a ZIP file.
 *
 * @author Sebastien Gerard
 */
@Document("snapshot")
@Getter
@Setter
@Accessors(chain = true)
public class SnapshotEntity {

    /**
     * The unique id of this snapshot.
     */
    @Id
    private String id;

    /**
     * The instant when this snapshot has been created.
     */
    @NotNull
    private Instant createdOn;

    /**
     * The display name of the user that asked this snapshot.
     */
    @NotNull
    private String createdBy;

    /**
     * Free end-user comment about this snapshot.
     */
    private String comment;

    /**
     * Location of the associated ZIP file.
     */
    @NotNull
    private String zipFile;

    /**
     * The original name of the ZIP file.
     */
    @NotNull
    private String originalFileName;

    /**
     * The password used to encrypt the ZIP file.
     */
    private String encryptionPassword;

    @PersistenceConstructor
    protected SnapshotEntity() {
    }

    public SnapshotEntity(Instant createdOn, String createdBy, String comment) {
        this.id = UUID.randomUUID().toString();
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.comment = comment;
    }

    /**
     * @see #comment
     */
    public Optional<String> getComment() {
        return Optional.ofNullable(comment);
    }

    /**
     * @see #encryptionPassword
     */
    public Optional<String> getEncryptionPassword() {
        return Optional.ofNullable(encryptionPassword);
    }

    /**
     * Returns the ZIP file of the snapshot.
     */
    public File getZipFileAsJavaFile() {
        return new File(getZipFile());
    }

    /**
     * Returns the {@link SnapshotMetadataDto metadata} of this snapshot.
     */
    public SnapshotMetadataDto toMetadata() {
        return SnapshotMetadataDto.builder()
                .id(getId())
                .createdBy(getCreatedBy())
                .createdOn(getCreatedOn())
                .comment(getComment().orElse(null))
                .build();
    }
}
