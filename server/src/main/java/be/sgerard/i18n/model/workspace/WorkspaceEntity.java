package be.sgerard.i18n.model.workspace;

import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.unmodifiableCollection;

/**
 * A workspace represents the edition of translations related to a particular branch of a repository. This
 * edition will be performed by end-users. At any time, end-users may decide to publish modifications to the repository.
 * Based on the repository, a review process may start, or modifications may be pushed directly to the repository's branch.
 *
 * @author Sebastien Gerard
 */
@Document("workspace")
public class WorkspaceEntity {

    @Id
    private String id;

    @NotNull
    private String repository;

    @NotNull
    private String branch;

    @NotNull
    private WorkspaceStatus status;

    private final Collection<BundleFileEntity> files = new HashSet<>();

    private AbstractReviewEntity review;

    @PersistenceConstructor
    WorkspaceEntity() {
    }

    public WorkspaceEntity(RepositoryEntity repository, String branch) {
        this.id = UUID.randomUUID().toString();

        this.repository = repository.getId();

        this.branch = branch;
        this.status = WorkspaceStatus.NOT_INITIALIZED;
    }

    /**
     * Returns the unique id of this workspace.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique id of this workspace.
     */
    void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the associated {@link RepositoryEntity repository}.
     */
    public String getRepository() {
        return repository;
    }

    /**
     * Sets the associated {@link RepositoryEntity repository}.
     */
    public void setRepository(String repository) {
        this.repository = repository;
    }

    /**
     * Returns the branch name of the repository containing those translations.
     */
    public String getBranch() {
        return branch;
    }

    /**
     * Sets the branch name of the repository containing those translations.
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }

    /**
     * Returns the current {@link WorkspaceStatus status}.
     */
    public WorkspaceStatus getStatus() {
        return status;
    }

    /**
     * Sets the current {@link WorkspaceStatus status}.
     */
    public void setStatus(WorkspaceStatus status) {
        this.status = status;
    }

    /**
     * Returns all the {@link BundleFileEntity files} compositing this workspace.
     */
    public Collection<BundleFileEntity> getFiles() {
        return unmodifiableCollection(files);
    }

    /**
     * Adds a {@link BundleFileEntity file} to this workspace.
     */
    public void addFile(BundleFileEntity file) {
        this.files.add(file);
    }

    /**
     * Returns information about the current {@link AbstractReviewEntity review}.
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
     * Sets information about the current {@link AbstractReviewEntity review}.
     */
    public WorkspaceEntity setReview(AbstractReviewEntity review) {
        this.review = review;
        return this;
    }
}
