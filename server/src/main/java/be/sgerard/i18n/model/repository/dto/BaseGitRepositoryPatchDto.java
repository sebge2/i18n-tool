package be.sgerard.i18n.model.repository.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

/**
 * Request asking the update of a Git repository.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "BaseGitRepositoryPatch", description = "Request asking the update of a Git repository")
public abstract class BaseGitRepositoryPatchDto extends RepositoryPatchDto {

    @Schema(description = "The name of the default branch used to find translations", required = true)
    private final String defaultBranch;


    @Schema(description = "Regex specifying branches that can be scanned by this tool.", required = true)
    private final String allowedBranches;

    protected BaseGitRepositoryPatchDto(BaseBuilder<?, ?> builder) {
        super(builder);

        this.defaultBranch = builder.defaultBranch;
        this.allowedBranches = builder.allowedBranches;
    }

    /**
     * @see #defaultBranch
     */
    public Optional<String> getDefaultBranch() {
        return Optional.ofNullable(defaultBranch);
    }

    /**
     * @see #allowedBranches
     */
    public Optional<String> getAllowedBranches() {
        return Optional.ofNullable(allowedBranches);
    }

    /**
     * Builder of {@link GitRepositoryPatchDto GIT repository patch DTO}.
     */
    public static abstract class BaseBuilder<R extends BaseGitRepositoryPatchDto, B extends BaseGitRepositoryPatchDto.BaseBuilder<R, B>> extends RepositoryPatchDto.BaseBuilder<R, B> {

        private String defaultBranch;
        private String allowedBranches;

        protected BaseBuilder() {
        }

        public B defaultBranch(String defaultBranch) {
            this.defaultBranch = defaultBranch;
            return self();
        }

        public B allowedBranches(String allowedBranches) {
            this.allowedBranches = allowedBranches;
            return self();
        }
    }
}
