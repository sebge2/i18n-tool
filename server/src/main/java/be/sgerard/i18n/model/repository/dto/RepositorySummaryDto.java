package be.sgerard.i18n.model.repository.dto;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Short description of the repository.
 *
 * @author Sebastien Gerard
 */
@ApiModel(description = "Description of the repository.")
@JsonDeserialize(builder = RepositorySummaryDto.Builder.class)
public class RepositorySummaryDto {

    public static Builder builder(RepositoryEntity repositoryEntity) {
        return builder()
                .id(repositoryEntity.getId())
                .name(repositoryEntity.getName())
                .status(repositoryEntity.getStatus());
    }

    public static Builder builder() {
        return new Builder();
    }

    @ApiModelProperty(notes = "Unique id of this repository.", required = true)
    private final String id;

    @ApiModelProperty(notes = "Current repository status.", required = true)
    private final RepositoryStatus status;

    @ApiModelProperty(notes = "Repository name", required = true)
    private final String name;

    private RepositorySummaryDto(Builder builder) {
        id = builder.id;
        status = builder.status;
        name = builder.name;
    }

    /**
     * Returns the unique id of this repository.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the current repository {@link RepositoryStatus status}.
     */
    public RepositoryStatus getStatus() {
        return status;
    }

    /**
     * Returns the repository name.
     */
    public String getName() {
        return name;
    }

    /**
     * Builder of {@link RepositorySummaryDto summary DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String id;
        private RepositoryStatus status;
        private String name;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder status(RepositoryStatus status) {
            this.status = status;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public RepositorySummaryDto build() {
            return new RepositorySummaryDto(this);
        }
    }
}
