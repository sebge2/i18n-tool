package be.sgerard.i18n.model.repository.snapshot;

import be.sgerard.i18n.model.repository.persistence.BaseGitRepositoryEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;

/**
 * Snapshot of a {@link BaseGitRepositoryEntity Git repository}.
 *
 * @author Sebastien Gerard
 */
@Getter
public abstract class BaseGitRepositorySnapshotDto extends RepositorySnapshotDto {

    /**
     * @see BaseGitRepositoryEntity#getLocation()
     */
    private final String location;

    /**
     * @see BaseGitRepositoryEntity#getDefaultBranch()
     */
    private final String defaultBranch;

    /**
     * @see BaseGitRepositoryEntity#getAllowedBranches()
     */
    public final String allowedBranches;

    protected BaseGitRepositorySnapshotDto(Builder builder) {
        super(builder);

        location = builder.location;
        defaultBranch = builder.defaultBranch;
        allowedBranches = builder.allowedBranches;
    }

    /**
     * Builder of {@link BaseGitRepositorySnapshotDto Git repository snapshot}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder extends RepositorySnapshotDto.Builder {

        private String location;
        private String defaultBranch;
        private String allowedBranches;

        protected Builder() {
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder defaultBranch(String defaultBranch) {
            this.defaultBranch = defaultBranch;
            return this;
        }

        public Builder allowedBranches(String allowedBranches) {
            this.allowedBranches = allowedBranches;
            return this;
        }
    }
}
