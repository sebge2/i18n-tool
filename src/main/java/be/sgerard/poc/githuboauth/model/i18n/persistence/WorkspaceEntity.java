package be.sgerard.poc.githuboauth.model.i18n.persistence;

import be.sgerard.poc.githuboauth.model.i18n.WorkspaceStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.unmodifiableCollection;

/**
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

    @Column
    private String pullRequestBranch;

    @Column
    private Integer pullRequestNumber;

    @Column
    private Instant initializationTime;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private Collection<BundleFileEntity> files = new HashSet<>();

    @Version
    private int version;

    WorkspaceEntity() {
    }

    public WorkspaceEntity(String branch) {
        this.id = UUID.randomUUID().toString();
        this.branch = branch;
        this.status = WorkspaceStatus.NOT_INITIALIZED;
    }

    public String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public WorkspaceStatus getStatus() {
        return status;
    }

    public void setStatus(WorkspaceStatus status) {
        this.status = status;
    }

    public Optional<String> getPullRequestBranch() {
        return Optional.ofNullable(pullRequestBranch);
    }

    public void setPullRequestBranch(String pullRequestBranch) {
        this.pullRequestBranch = pullRequestBranch;
    }

    public Optional<Integer> getPullRequestNumber() {
        return Optional.ofNullable(pullRequestNumber);
    }

    public void setPullRequestNumber(Integer pullRequestNumber) {
        this.pullRequestNumber = pullRequestNumber;
    }

    public Optional<Instant> getInitializationTime() {
        return Optional.ofNullable(initializationTime);
    }

    public void setInitializationTime(Instant initializationTime) {
        this.initializationTime = initializationTime;
    }

    public Collection<BundleFileEntity> getFiles() {
        return unmodifiableCollection(files);
    }

    void addFile(BundleFileEntity file) {
        this.files.add(file);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
