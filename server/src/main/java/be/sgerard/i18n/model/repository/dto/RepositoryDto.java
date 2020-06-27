package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.RepositoryType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Repository that can be of different type. A repository contains translations.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "Repository", description = "Repository that can be of different type.")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GitRepositoryDto.class, name = "GIT"),
        @JsonSubTypes.Type(value = GitHubRepositoryDto.class, name = "GITHUB")
})
public abstract class RepositoryDto {

    private final String id;
    private final String name;
    private final RepositoryStatus status;

    protected RepositoryDto(BaseBuilder<?, ?> builder) {
        id = builder.id;
        name = builder.name;
        status = builder.status;
    }

    /**
     * Returns the {@link RepositoryType type} of this repository.
     */
    public abstract RepositoryType getType();

    /**
     * Returns the {@link RepositoryType type} of this repository.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the display name to use for this repository.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the {@link RepositoryStatus current status}.
     */
    public RepositoryStatus getStatus() {
        return status;
    }

    /**
     * Builder of {@link RepositoryDto repository DTO}.
     */
    public static abstract class BaseBuilder<R extends RepositoryDto, B extends BaseBuilder<R, B>> {
        private String id;
        private String name;
        private RepositoryStatus status;

        protected BaseBuilder() {
        }

        public B id(String id) {
            this.id = id;
            return self();
        }

        public B name(String name) {
            this.name = name;
            return self();
        }

        public B status(RepositoryStatus status) {
            this.status = status;
            return self();
        }

        public abstract R build();

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }
    }

}
