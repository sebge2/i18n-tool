package be.sgerard.i18n.model.repository;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Sebastien Gerard
 */
@ApiModel(description = "Description of the repository.")
public class RepositoryDescriptionDto {

    @ApiModelProperty(notes = "URI of the remote repository.", required = true)
    private final String uri;

    @ApiModelProperty(notes = "Current repository status.", required = true)
    private final RepositoryStatus status;

    public RepositoryDescriptionDto(String uri, RepositoryStatus status) {
        this.uri = uri;
        this.status = status;
    }

    public String getUri() {
        return uri;
    }

    public RepositoryStatus getStatus() {
        return status;
    }
}
