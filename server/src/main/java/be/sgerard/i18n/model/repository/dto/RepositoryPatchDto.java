package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request asking the creation of a repository.
 *
 * @author Sebastien Gerard
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GitRepositoryPatchDto.class, name = "GIT"),
        @JsonSubTypes.Type(value = GitHubRepositoryPatchDto.class, name = "GITHUB")
})
@Schema(name = "RepositoryPatchRequest", description = "Request asking the creation of a repository")
public abstract class RepositoryPatchDto {

    @Schema(description = "The id of the repository to modify", required = true)
    private final String id;

    protected RepositoryPatchDto(BaseBuilder<?, ?> builder) {
        this.id = builder.id;
    }

    /**
     * Returns the {@link RepositoryType type} of this repository.
     */
    @Schema(description = "Type of this repository", required = true)
    public abstract RepositoryType getType();

    /**
     * Returns unique id of the repository to modify.
     */
    public String getId() {
        return id;
    }

    /**
     * Builder of {@link RepositoryPatchDto repository patch DTO}.
     */
    public static abstract class BaseBuilder<R extends RepositoryPatchDto, B extends RepositoryPatchDto.BaseBuilder<R, B>> {

        private String id;

        protected BaseBuilder() {
        }

        public B id(String id) {
            this.id = id;
            return self();
        }

        public abstract R build();

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }
    }
}
