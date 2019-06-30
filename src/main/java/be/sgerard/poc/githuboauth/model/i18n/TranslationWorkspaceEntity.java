package be.sgerard.poc.githuboauth.model.i18n;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Sebastien Gerard
 */
@Entity(name = "BranchTranslationWorkspace")
public class TranslationWorkspaceEntity {

    @Id
    private String id;

    @NotNull
    @Column(nullable = false)
    private String branch;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TranslationWorkspaceStatus status;

    @Column
    private String pullRequestBranch;

    @Column
    private Integer pullRequestNumber;

    @Column
    private Instant loadingTime;

    @Version
    private int version;

    TranslationWorkspaceEntity() {
    }

    public TranslationWorkspaceEntity(String branch) {
        this.id = UUID.randomUUID().toString();
        this.branch = branch;
        this.status = TranslationWorkspaceStatus.AVAILABLE;
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

    public TranslationWorkspaceStatus getStatus() {
        return status;
    }

    public void setStatus(TranslationWorkspaceStatus status) {
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

    public Instant getLoadingTime() {
        return loadingTime;
    }

    public void setLoadingTime(Instant loadingTime) {
        this.loadingTime = loadingTime;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
