package be.sgerard.i18n.model.repository.snapshot;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.user.snapshot.UserSnapshotDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;

/**
 * Snapshot of a {@link RepositoryEntity repository}.
 *
 * @author Sebastien Gerard
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GitRepositorySnapshotDto.class, name = "GIT"),
        @JsonSubTypes.Type(value = GitHubRepositorySnapshotDto.class, name = "GIT_HUB")
})
@Getter
public abstract class RepositorySnapshotDto {

    /**
     * @see RepositoryEntity#getId()
     */
    private final String id;

    /**
     * @see RepositoryEntity#getName()
     */
    private final String name;

    /**
     * @see RepositoryEntity#getStatus()
     */
    private final RepositoryStatus status;

    /**
     * @see RepositoryEntity#getTranslationsConfiguration()
     */
    private final TranslationsConfigurationSnapshotDto translationsConfiguration;

    protected RepositorySnapshotDto(Builder builder) {
        id = builder.id;
        name = builder.name;
        status = builder.status;
        translationsConfiguration = builder.translationsConfiguration;
    }

    /**
     * Returns the {@link Type type} of this repository.
     */
    public abstract Type getType();

    /**
     * Builder of {@link UserSnapshotDto user}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder {

        private String id;
        private String name;
        private RepositoryStatus status;
        private TranslationsConfigurationSnapshotDto translationsConfiguration = TranslationsConfigurationSnapshotDto.builder().build();

        protected Builder() {
        }

        public abstract RepositorySnapshotDto build();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder status(RepositoryStatus status) {
            this.status = status;
            return this;
        }

        public Builder translationsConfiguration(TranslationsConfigurationSnapshotDto translationsConfiguration) {
            this.translationsConfiguration = translationsConfiguration;
            return this;
        }
    }

    /**
     * @see be.sgerard.i18n.model.repository.RepositoryStatus
     */
    public enum RepositoryStatus {

        NOT_INITIALIZED,
        INITIALIZED,
        INITIALIZATION_ERROR
    }


    /**
     * All possible repository types.
     */
    public enum Type {

        GIT,
        GIT_HUB

    }
}
