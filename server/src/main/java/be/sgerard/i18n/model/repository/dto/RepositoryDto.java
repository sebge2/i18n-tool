package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.RepositoryType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

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
@Getter
public abstract class RepositoryDto {

    @Schema(description = "Unique id of this repository.", required = true)
    private final String id;

    @Schema(description = "Display name to use for this repository.", required = true)
    private final String name;

    @Schema(description = "The current repository status.", required = true)
    private final RepositoryStatus status;

    @Schema(description = "The configuration to use for managing translations", required = true)
    private final TranslationsConfigurationDto translationsConfiguration;

    protected RepositoryDto(BaseBuilder<?, ?> builder) {
        id = builder.id;
        name = builder.name;
        status = builder.status;
        translationsConfiguration = builder.translationsConfiguration;
    }

    /**
     * Returns the {@link RepositoryType type} of this repository.
     */
    public abstract RepositoryType getType();

    /**
     * Builder of {@link RepositoryDto repository DTO}.
     */
    public static abstract class BaseBuilder<R extends RepositoryDto, B extends BaseBuilder<R, B>> {
        private String id;
        private String name;
        private RepositoryStatus status;
        private TranslationsConfigurationDto translationsConfiguration;

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

        public B translationsConfiguration(TranslationsConfigurationDto translationsConfiguration) {
            this.translationsConfiguration = translationsConfiguration;
            return self();
        }

        public abstract R build();

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }
    }

}
