package be.sgerard.poc.githuboauth.model.repository;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Sebastien Gerard
 */
@ApiModel(description = "Description of the repository.")
public class RepositoryDescriptionDto {

    @ApiModelProperty(notes = "URI of the remote repository.", required = true)
    private final String uri;

    @ApiModelProperty(notes = "Flag indicating whether the repository is initialized.", required = true)
    private final boolean initialized;

    public RepositoryDescriptionDto(String uri, boolean initialized) {
        this.uri = uri;
        this.initialized = initialized;
    }

    public String getUri() {
        return uri;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
