package be.sgerard.i18n.model.git;

import be.sgerard.i18n.model.github.GitHubPullRequestEventDto;
import be.sgerard.i18n.model.github.GitHubPullRequestStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Sebastien Gerard
 */
public class GitHubPullRequestDeserializedEventDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void deserialize() throws IOException {
        final GitHubPullRequestEventDto actual = objectMapper.readValue("{\n" +
            "  \"action\": \"closed\",\n" +
            "  \"number\": 19,\n" +
            "  \"pull_request\": {\n" +
            "    \"id\": 456,\n" +
            "    \"number\": 19,\n" +
            "    \"state\": \"closed\",\n" +
            "    \"locked\": false,\n" +
            "    \"body\": null,\n" +
            "    \"merged_at\": null,\n" +
            "    \"assignee\": null,\n" +
            "    \"assignees\": [\n" +
            "\n" +
            "    ],\n" +
            "    \"requested_reviewers\": [\n" +
            "\n" +
            "    ],\n" +
            "    \"requested_teams\": [\n" +
            "\n" +
            "    ],\n" +
            "    \"labels\": [\n" +
            "\n" +
            "    ],\n" +
            "    \"head\": {\n" +
            "      \"repo\": {\n" +
            "        \"private\": true,\n" +
            "        \"description\": null,\n" +
            "        \"fork\": false,\n" +
            "        \"homepage\": null,\n" +
            "        \"size\": 12,\n" +
            "        \"stargazers_count\": 0,\n" +
            "        \"watchers_count\": 0,\n" +
            "        \"language\": \"Java\",\n" +
            "        \"has_issues\": true,\n" +
            "        \"has_projects\": true,\n" +
            "        \"has_downloads\": true,\n" +
            "        \"has_wiki\": true,\n" +
            "        \"has_pages\": false,\n" +
            "        \"forks_count\": 0,\n" +
            "        \"mirror_url\": null,\n" +
            "        \"archived\": false,\n" +
            "        \"disabled\": false,\n" +
            "        \"open_issues_count\": 0,\n" +
            "        \"license\": null,\n" +
            "        \"forks\": 0,\n" +
            "        \"open_issues\": 0,\n" +
            "        \"watchers\": 0,\n" +
            "        \"default_branch\": \"master\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"base\": {\n" +
            "      \"ref\": \"master\",\n" +
            "      \"user\": {\n" +
            "        \"gravatar_id\": \"\",\n" +
            "        \"type\": \"User\",\n" +
            "        \"site_admin\": false\n" +
            "      },\n" +
            "      \"repo\": {\n" +
            "        \"private\": true,\n" +
            "        \"owner\": {\n" +
            "          \"gravatar_id\": \"\",\n" +
            "          \"type\": \"User\",\n" +
            "          \"site_admin\": false\n" +
            "        },\n" +
            "        \"description\": null,\n" +
            "        \"fork\": false,\n" +
            "        \"homepage\": null,\n" +
            "        \"size\": 12,\n" +
            "        \"stargazers_count\": 0,\n" +
            "        \"watchers_count\": 0,\n" +
            "        \"language\": \"Java\",\n" +
            "        \"has_issues\": true,\n" +
            "        \"has_projects\": true,\n" +
            "        \"has_downloads\": true,\n" +
            "        \"has_wiki\": true,\n" +
            "        \"has_pages\": false,\n" +
            "        \"forks_count\": 0,\n" +
            "        \"mirror_url\": null,\n" +
            "        \"archived\": false,\n" +
            "        \"disabled\": false,\n" +
            "        \"open_issues_count\": 0,\n" +
            "        \"license\": null,\n" +
            "        \"forks\": 0,\n" +
            "        \"open_issues\": 0,\n" +
            "        \"watchers\": 0,\n" +
            "        \"default_branch\": \"master\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"_links\": {\n" +
            "    },\n" +
            "    \"author_association\": \"OWNER\",\n" +
            "    \"draft\": false,\n" +
            "    \"merged\": false,\n" +
            "    \"mergeable\": true,\n" +
            "    \"rebaseable\": true,\n" +
            "    \"mergeable_state\": \"clean\",\n" +
            "    \"merged_by\": null,\n" +
            "    \"comments\": 0,\n" +
            "    \"review_comments\": 0,\n" +
            "    \"maintainer_can_modify\": false,\n" +
            "    \"commits\": 1,\n" +
            "    \"additions\": 6,\n" +
            "    \"deletions\": 6,\n" +
            "    \"changed_files\": 3\n" +
            "  },\n" +
            "  \"repository\": {\n" +
            "    \"private\": true,\n" +
            "    \"owner\": {\n" +
            "      \"gravatar_id\": \"\",\n" +
            "      \"type\": \"User\",\n" +
            "      \"site_admin\": false\n" +
            "    },\n" +
            "    \"description\": null,\n" +
            "    \"fork\": false,\n" +
            "    \"homepage\": null,\n" +
            "    \"size\": 12,\n" +
            "    \"stargazers_count\": 0,\n" +
            "    \"watchers_count\": 0,\n" +
            "    \"language\": \"Java\",\n" +
            "    \"has_issues\": true,\n" +
            "    \"has_projects\": true,\n" +
            "    \"has_downloads\": true,\n" +
            "    \"has_wiki\": true,\n" +
            "    \"has_pages\": false,\n" +
            "    \"forks_count\": 0,\n" +
            "    \"mirror_url\": null,\n" +
            "    \"archived\": false,\n" +
            "    \"disabled\": false,\n" +
            "    \"open_issues_count\": 0,\n" +
            "    \"license\": null,\n" +
            "    \"forks\": 0,\n" +
            "    \"open_issues\": 0,\n" +
            "    \"watchers\": 0,\n" +
            "    \"default_branch\": \"master\"\n" +
            "  },\n" +
            "  \"sender\": {\n" +
            "    \"type\": \"User\",\n" +
            "    \"site_admin\": false\n" +
            "  }\n" +
            "}", GitHubPullRequestEventDto.class);

        assertEquals("id", "456", actual.getId());
        assertEquals("number", 19, actual.getNumber());
        assertEquals("status", GitHubPullRequestStatus.CLOSED, actual.getStatus());
    }

}
