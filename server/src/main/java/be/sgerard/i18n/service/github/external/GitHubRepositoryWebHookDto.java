package be.sgerard.i18n.service.github.external;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Information about the current repository.
 *
 * @author Sebastien Gerard
 */
public class GitHubRepositoryWebHookDto {

    private final String id;
    private final String fullName;

    @JsonCreator
    public GitHubRepositoryWebHookDto(@JsonProperty("id") String id,
                                      @JsonProperty("full_name") String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    /**
     * Returns the unique id of this repository.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the full name of this repository (ex: sebge2/i18n-tool).
     */
    public String getFullName() {
        return fullName;
    }
}
