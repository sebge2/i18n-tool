package be.sgerard.i18n.model.github.external;

import be.sgerard.i18n.model.repository.github.external.GitHubBranchDeletedEventDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
public class GitHubBranchDeletedEventDtoTest {

    @Test
    public void deserialize() throws JsonProcessingException {
        final GitHubBranchDeletedEventDto event = new ObjectMapper().readValue(getSampleEvent(), GitHubBranchDeletedEventDto.class);

        assertThat(event.getRef()).isEqualTo("simple-tag");
        assertThat(event.getRefType()).isEqualTo("tag");
        assertThat(event.getRepository().getId()).isEqualTo("186853002");
        assertThat(event.getRepository().getFullName()).isEqualTo("Codertocat/Hello-World");
    }

    private String getSampleEvent() {
        return "{\n" +
                "  \"ref\": \"simple-tag\",\n" +
                "  \"ref_type\": \"tag\",\n" +
                "  \"pusher_type\": \"user\",\n" +
                "  \"repository\": {\n" +
                "    \"id\": 186853002,\n" +
                "    \"node_id\": \"MDEwOlJlcG9zaXRvcnkxODY4NTMwMDI=\",\n" +
                "    \"name\": \"Hello-World\",\n" +
                "    \"full_name\": \"Codertocat/Hello-World\",\n" +
                "    \"private\": false,\n" +
                "    \"owner\": {\n" +
                "      \"login\": \"Codertocat\",\n" +
                "      \"id\": 21031067,\n" +
                "      \"node_id\": \"MDQ6VXNlcjIxMDMxMDY3\",\n" +
                "      \"avatar_url\": \"https://avatars1.githubusercontent.com/u/21031067?v=4\",\n" +
                "      \"gravatar_id\": \"\",\n" +
                "      \"url\": \"https://api.github.com/users/Codertocat\",\n" +
                "      \"html_url\": \"https://github.com/Codertocat\",\n" +
                "      \"followers_url\": \"https://api.github.com/users/Codertocat/followers\",\n" +
                "      \"following_url\": \"https://api.github.com/users/Codertocat/following{/other_user}\",\n" +
                "      \"gists_url\": \"https://api.github.com/users/Codertocat/gists{/gist_id}\",\n" +
                "      \"starred_url\": \"https://api.github.com/users/Codertocat/starred{/owner}{/repo}\",\n" +
                "      \"subscriptions_url\": \"https://api.github.com/users/Codertocat/subscriptions\",\n" +
                "      \"organizations_url\": \"https://api.github.com/users/Codertocat/orgs\",\n" +
                "      \"repos_url\": \"https://api.github.com/users/Codertocat/repos\",\n" +
                "      \"events_url\": \"https://api.github.com/users/Codertocat/events{/privacy}\",\n" +
                "      \"received_events_url\": \"https://api.github.com/users/Codertocat/received_events\",\n" +
                "      \"type\": \"User\",\n" +
                "      \"site_admin\": false\n" +
                "    },\n" +
                "    \"html_url\": \"https://github.com/Codertocat/Hello-World\",\n" +
                "    \"description\": null,\n" +
                "    \"fork\": false,\n" +
                "    \"url\": \"https://api.github.com/repos/Codertocat/Hello-World\",\n" +
                "    \"forks_url\": \"https://api.github.com/repos/Codertocat/Hello-World/forks\",\n" +
                "    \"keys_url\": \"https://api.github.com/repos/Codertocat/Hello-World/keys{/key_id}\",\n" +
                "    \"collaborators_url\": \"https://api.github.com/repos/Codertocat/Hello-World/collaborators{/collaborator}\",\n" +
                "    \"teams_url\": \"https://api.github.com/repos/Codertocat/Hello-World/teams\",\n" +
                "    \"hooks_url\": \"https://api.github.com/repos/Codertocat/Hello-World/hooks\",\n" +
                "    \"issue_events_url\": \"https://api.github.com/repos/Codertocat/Hello-World/issues/events{/number}\",\n" +
                "    \"events_url\": \"https://api.github.com/repos/Codertocat/Hello-World/events\",\n" +
                "    \"assignees_url\": \"https://api.github.com/repos/Codertocat/Hello-World/assignees{/user}\",\n" +
                "    \"branches_url\": \"https://api.github.com/repos/Codertocat/Hello-World/branches{/branch}\",\n" +
                "    \"tags_url\": \"https://api.github.com/repos/Codertocat/Hello-World/tags\",\n" +
                "    \"blobs_url\": \"https://api.github.com/repos/Codertocat/Hello-World/git/blobs{/sha}\",\n" +
                "    \"git_tags_url\": \"https://api.github.com/repos/Codertocat/Hello-World/git/tags{/sha}\",\n" +
                "    \"git_refs_url\": \"https://api.github.com/repos/Codertocat/Hello-World/git/refs{/sha}\",\n" +
                "    \"trees_url\": \"https://api.github.com/repos/Codertocat/Hello-World/git/trees{/sha}\",\n" +
                "    \"statuses_url\": \"https://api.github.com/repos/Codertocat/Hello-World/statuses/{sha}\",\n" +
                "    \"languages_url\": \"https://api.github.com/repos/Codertocat/Hello-World/languages\",\n" +
                "    \"stargazers_url\": \"https://api.github.com/repos/Codertocat/Hello-World/stargazers\",\n" +
                "    \"contributors_url\": \"https://api.github.com/repos/Codertocat/Hello-World/contributors\",\n" +
                "    \"subscribers_url\": \"https://api.github.com/repos/Codertocat/Hello-World/subscribers\",\n" +
                "    \"subscription_url\": \"https://api.github.com/repos/Codertocat/Hello-World/subscription\",\n" +
                "    \"commits_url\": \"https://api.github.com/repos/Codertocat/Hello-World/commits{/sha}\",\n" +
                "    \"git_commits_url\": \"https://api.github.com/repos/Codertocat/Hello-World/git/commits{/sha}\",\n" +
                "    \"comments_url\": \"https://api.github.com/repos/Codertocat/Hello-World/comments{/number}\",\n" +
                "    \"issue_comment_url\": \"https://api.github.com/repos/Codertocat/Hello-World/issues/comments{/number}\",\n" +
                "    \"contents_url\": \"https://api.github.com/repos/Codertocat/Hello-World/contents/{+path}\",\n" +
                "    \"compare_url\": \"https://api.github.com/repos/Codertocat/Hello-World/compare/{base}...{head}\",\n" +
                "    \"merges_url\": \"https://api.github.com/repos/Codertocat/Hello-World/merges\",\n" +
                "    \"archive_url\": \"https://api.github.com/repos/Codertocat/Hello-World/{archive_format}{/ref}\",\n" +
                "    \"downloads_url\": \"https://api.github.com/repos/Codertocat/Hello-World/downloads\",\n" +
                "    \"issues_url\": \"https://api.github.com/repos/Codertocat/Hello-World/issues{/number}\",\n" +
                "    \"pulls_url\": \"https://api.github.com/repos/Codertocat/Hello-World/pulls{/number}\",\n" +
                "    \"milestones_url\": \"https://api.github.com/repos/Codertocat/Hello-World/milestones{/number}\",\n" +
                "    \"notifications_url\": \"https://api.github.com/repos/Codertocat/Hello-World/notifications{?since,all,participating}\",\n" +
                "    \"labels_url\": \"https://api.github.com/repos/Codertocat/Hello-World/labels{/name}\",\n" +
                "    \"releases_url\": \"https://api.github.com/repos/Codertocat/Hello-World/releases{/id}\",\n" +
                "    \"deployments_url\": \"https://api.github.com/repos/Codertocat/Hello-World/deployments\",\n" +
                "    \"created_at\": \"2019-05-15T15:19:25Z\",\n" +
                "    \"updated_at\": \"2019-05-15T15:20:41Z\",\n" +
                "    \"pushed_at\": \"2019-05-15T15:20:57Z\",\n" +
                "    \"git_url\": \"git://github.com/Codertocat/Hello-World.git\",\n" +
                "    \"ssh_url\": \"git@github.com:Codertocat/Hello-World.git\",\n" +
                "    \"clone_url\": \"https://github.com/Codertocat/Hello-World.git\",\n" +
                "    \"svn_url\": \"https://github.com/Codertocat/Hello-World\",\n" +
                "    \"homepage\": null,\n" +
                "    \"size\": 0,\n" +
                "    \"stargazers_count\": 0,\n" +
                "    \"watchers_count\": 0,\n" +
                "    \"language\": \"Ruby\",\n" +
                "    \"has_issues\": true,\n" +
                "    \"has_projects\": true,\n" +
                "    \"has_downloads\": true,\n" +
                "    \"has_wiki\": true,\n" +
                "    \"has_pages\": true,\n" +
                "    \"forks_count\": 1,\n" +
                "    \"mirror_url\": null,\n" +
                "    \"archived\": false,\n" +
                "    \"disabled\": false,\n" +
                "    \"open_issues_count\": 2,\n" +
                "    \"license\": null,\n" +
                "    \"forks\": 1,\n" +
                "    \"open_issues\": 2,\n" +
                "    \"watchers\": 0,\n" +
                "    \"default_branch\": \"master\"\n" +
                "  },\n" +
                "  \"sender\": {\n" +
                "    \"login\": \"Codertocat\",\n" +
                "    \"id\": 21031067,\n" +
                "    \"node_id\": \"MDQ6VXNlcjIxMDMxMDY3\",\n" +
                "    \"avatar_url\": \"https://avatars1.githubusercontent.com/u/21031067?v=4\",\n" +
                "    \"gravatar_id\": \"\",\n" +
                "    \"url\": \"https://api.github.com/users/Codertocat\",\n" +
                "    \"html_url\": \"https://github.com/Codertocat\",\n" +
                "    \"followers_url\": \"https://api.github.com/users/Codertocat/followers\",\n" +
                "    \"following_url\": \"https://api.github.com/users/Codertocat/following{/other_user}\",\n" +
                "    \"gists_url\": \"https://api.github.com/users/Codertocat/gists{/gist_id}\",\n" +
                "    \"starred_url\": \"https://api.github.com/users/Codertocat/starred{/owner}{/repo}\",\n" +
                "    \"subscriptions_url\": \"https://api.github.com/users/Codertocat/subscriptions\",\n" +
                "    \"organizations_url\": \"https://api.github.com/users/Codertocat/orgs\",\n" +
                "    \"repos_url\": \"https://api.github.com/users/Codertocat/repos\",\n" +
                "    \"events_url\": \"https://api.github.com/users/Codertocat/events{/privacy}\",\n" +
                "    \"received_events_url\": \"https://api.github.com/users/Codertocat/received_events\",\n" +
                "    \"type\": \"User\",\n" +
                "    \"site_admin\": false\n" +
                "  }\n" +
                "}";
    }

}
