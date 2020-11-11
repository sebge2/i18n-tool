package be.sgerard.i18n.model.repository.github.external;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Organization in GitHub.
 *
 * @author Sebastien Gerard
 */
@Getter
public class GitHubOrganizationDto {

    /**
     * Unique id of the account.
     */
    private final String id;

    /**
     * Login displayed to the end-user.
     */
    private final String login;

    @JsonCreator
    public GitHubOrganizationDto(@JsonProperty("id") String id,
                                 @JsonProperty("login") String login) {
        this.id = id;
        this.login = login;
    }
}
