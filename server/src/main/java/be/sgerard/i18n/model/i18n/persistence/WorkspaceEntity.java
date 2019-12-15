package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.i18n.WorkspaceStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import static java.util.Collections.unmodifiableCollection;

/**
 * A workspace represents the edition of translations related to a particular branch of a repository. This
 * edition will be performed by end-users. At any time, end-users may decide to publish modifications to the repository.
 * Based on the repository, a review process may start, or modifications may be pushed directly to the repository's branch.
 *
 * @author Sebastien Gerard
 */
@Entity(name = "translation_workspace")
public class WorkspaceEntity {

    @Id
    private String id;

    @NotNull
    @Column(nullable = false)
    private String branch;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkspaceStatus status;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private final Collection<BundleFileEntity> files = new HashSet<>();

    @Version
    private int version;

    WorkspaceEntity() {
    }

    public WorkspaceEntity(String branch) {
        this.id = UUID.randomUUID().toString();
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
    void addFile(BundleFileEntity file) {
        this.files.add(file);
    }
}
