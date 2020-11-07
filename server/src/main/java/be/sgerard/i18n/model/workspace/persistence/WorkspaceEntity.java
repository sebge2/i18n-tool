package be.sgerard.i18n.model.workspace.persistence;

import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.*;

/**
 * A workspace represents the edition of translations related to a particular branch of a repository. This
 * edition will be performed by end-users. At any time, end-users may decide to publish modifications to the repository.
 * Based on the repository, a review process may start, or modifications may be pushed directly to the repository's branch.
 *
 * @author Sebastien Gerard
 */
@Document("workspace")
@Getter
@Setter
@Accessors(chain = true)
public class WorkspaceEntity {

    /**
     * The unique id of this workspace.
     */
    @Id
    private String id;

    /**
     * The associated {@link RepositoryEntity repository}.
     */
    @NotNull
    private String repository;

    /**
     * The branch name of the repository containing those translations.
     */
    @NotNull
    private String branch;

    /**
     * The current {@link WorkspaceStatus status}.
     */
    @NotNull
    private WorkspaceStatus status;

    /**
     * The {@link BundleFileEntity files} compositing this workspace.
     */
    @AccessType(AccessType.Type.PROPERTY)
    private Collection<BundleFileEntity> files = new HashSet<>();

    /**
     * The time when the workspace was synchronized with the repository for the last time. Only available
     * if the workspace is {@link WorkspaceStatus#INITIALIZED initialized}.
     */
    private Instant lastSynchronization;

    /**
     * Information about the current {@link AbstractReviewEntity review}.
     */
    private AbstractReviewEntity review;

    @PersistenceConstructor
    WorkspaceEntity() {
    }

    public WorkspaceEntity(String repository, String branch) {
        this.id = UUID.randomUUID().toString();

        this.repository = repository;

        this.branch = branch;
        this.status = WorkspaceStatus.NOT_INITIALIZED;
    }

    /**
     * @see #files
     */
    public void addFile(BundleFileEntity file) {
        this.files.add(file);
    }

    /**
     * @see #files
     */
    public void removeFile(BundleFileEntity file) {
        this.files.remove(file);
    }

    /**
     * @see #lastSynchronization
     */
    public Optional<Instant> getLastSynchronization() {
        return Optional.ofNullable(lastSynchronization);
    }

    /**
     * @see #review
     */
    public Optional<AbstractReviewEntity> getReview() {
        return Optional.ofNullable(review);
    }

    /**
     * Returns information about the current {@link AbstractReviewEntity review}.
     */
    public <R extends AbstractReviewEntity> Optional<R> getReview(Class<R> reviewType) {
        return getReview().map(reviewType::cast);
    }

    /**
     * Returns information about the current {@link AbstractReviewEntity review}.
     */
    public <R extends AbstractReviewEntity> R getReviewOrDie(Class<R> reviewType) {
        return getReview(reviewType).orElseThrow(() -> new IllegalStateException("There is no associated review entity."));
    }

    /**
     * Returns, or creates the {@link BundleFileEntity bundle file} for the newly scanned {@link ScannedBundleFile bundle file}.
     */
    public BundleFileEntity getOrCreateBundleFile(ScannedBundleFile bundleFile) {
        return getFiles().stream()
                .filter(file ->
                        Objects.equals(bundleFile.getType(), file.getType())
                                && Objects.equals(bundleFile.getName(), file.getName())
                                && Objects.equals(bundleFile.getLocationDirectory().toString(), file.getLocation())
                )
                .findFirst()
                .orElseGet(() -> {
                    final BundleFileEntity bundleFileEntity = new BundleFileEntity(bundleFile);

                    addFile(bundleFileEntity);

                    return bundleFileEntity;
                });
    }

    @Override
    public String toString() {
        return "WorkspaceEntity{" +
                "repository='" + repository + '\'' +
                ", branch='" + branch + '\'' +
                '}';
    }
}
