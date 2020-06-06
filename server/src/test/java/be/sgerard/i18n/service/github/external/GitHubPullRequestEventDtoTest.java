package be.sgerard.i18n.service.github.external;

import be.sgerard.i18n.model.github.GitHubPullRequestStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
public class GitHubPullRequestEventDtoTest {

    @Test
    public void deserialize() throws JsonProcessingException {
        final GitHubPullRequestEventDto event = new ObjectMapper().readValue(getSampleEvent(), GitHubPullRequestEventDto.class);

        assertThat(event.getPullRequest().getId()).isEqualTo("279147437");
        assertThat(event.getPullRequest().getNumber()).isEqualTo(2);
        assertThat(event.getPullRequest().getStatus()).isEqualTo(GitHubPullRequestStatus.OPEN);
        assertThat(event.getRepository().getId()).isEqualTo("186853002");
        assertThat(event.getRepository().getFullName()).isEqualTo("Codertocat/Hello-World");
    }

    private String getSampleEvent() {
        return "{\n" +
                "  \"action\": \"opened\",\n" +
                "  \"number\": 2,\n" +
                "  \"pull_request\": {\n" +
                "    \"url\": \"https://api.github.com/repos/Codertocat/Hello-World/pulls/2\",\n" +
                "    \"id\": 279147437,\n" +
                "    \"node_id\": \"MDExOlB1bGxSZXF1ZXN0Mjc5MTQ3NDM3\",\n" +
                "    \"html_url\": \"https://github.com/Codertocat/Hello-World/pull/2\",\n" +
                "    \"diff_url\": \"https://github.com/Codertocat/Hello-World/pull/2.diff\",\n" +
                "    \"patch_url\": \"https://github.com/Codertocat/Hello-World/pull/2.patch\",\n" +
                "    \"issue_url\": \"https://api.github.com/repos/Codertocat/Hello-World/issues/2\",\n" +
                "    \"number\": 2,\n" +
                "    \"state\": \"open\",\n" +
                "    \"locked\": false,\n" +
                "    \"title\": \"Update the README with new information.\",\n" +
                "    \"user\": {\n" +
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
                "    \"body\": \"This is a pretty simple change that we need to pull into master.\",\n" +
                "    \"created_at\": \"2019-05-15T15:20:33Z\",\n" +
                "    \"updated_at\": \"2019-05-15T15:20:33Z\",\n" +
                "    \"closed_at\": null,\n" +
                "    \"merged_at\": null,\n" +
                "    \"merge_commit_sha\": null,\n" +
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
                "    \"milestone\": null,\n" +
                "    \"commits_url\": \"https://api.github.com/repos/Codertocat/Hello-World/pulls/2/commits\",\n" +
                "    \"review_comments_url\": \"https://api.github.com/repos/Codertocat/Hello-World/pulls/2/comments\",\n" +
                "    \"review_comment_url\": \"https://api.github.com/repos/Codertocat/Hello-World/pulls/comments{/number}\",\n" +
                "    \"comments_url\": \"https://api.github.com/repos/Codertocat/Hello-World/issues/2/comments\",\n" +
                "    \"statuses_url\": \"https://api.github.com/repos/Codertocat/Hello-World/statuses/ec26c3e57ca3a959ca5aad62de7213c562f8c821\",\n" +
                "    \"head\": {\n" +
                "      \"label\": \"Codertocat:changes\",\n" +
                "      \"ref\": \"changes\",\n" +
                "      \"sha\": \"ec26c3e57ca3a959ca5aad62de7213c562f8c821\",\n" +
                "      \"user\": {\n" +
                "        \"login\": \"Codertocat\",\n" +
                "        \"id\": 21031067,\n" +
                "        \"node_id\": \"MDQ6VXNlcjIxMDMxMDY3\",\n" +
                "        \"avatar_url\": \"https://avatars1.githubusercontent.com/u/21031067?v=4\",\n" +
                "        \"gravatar_id\": \"\",\n" +
                "        \"url\": \"https://api.github.com/users/Codertocat\",\n" +
                "        \"html_url\": \"https://github.com/Codertocat\",\n" +
                "        \"followers_url\": \"https://api.github.com/users/Codertocat/followers\",\n" +
                "        \"following_url\": \"https://api.github.com/users/Codertocat/following{/other_user}\",\n" +
                "        \"gists_url\": \"https://api.github.com/users/Codertocat/gists{/gist_id}\",\n" +
                "        \"starred_url\": \"https://api.github.com/users/Codertocat/starred{/owner}{/repo}\",\n" +
                "        \"subscriptions_url\": \"https://api.github.com/users/Codertocat/subscriptions\",\n" +
                "        \"organizations_url\": \"https://api.github.com/users/Codertocat/orgs\",\n" +
                "        \"repos_url\": \"https://api.github.com/users/Codertocat/repos\",\n" +
                "        \"events_url\": \"https://api.github.com/users/Codertocat/events{/privacy}\",\n" +
                "        \"received_events_url\": \"https://api.github.com/users/Codertocat/received_events\",\n" +
                "        \"type\": \"User\",\n" +
                "        \"site_admin\": false\n" +
                "      },\n" +
                "      \"repo\": {\n" +
                "        \"id\": 186853002,\n" +
                "        \"node_id\": \"MDEwOlJlcG9zaXRvcnkxODY4NTMwMDI=\",\n" +
                "        \"name\": \"Hello-World\",\n" +
                "        \"full_name\": \"Codertocat/Hello-World\",\n" +
                "        \"private\": false,\n" +
                "        \"owner\": {\n" +
                "          \"login\": \"Codertocat\",\n" +
                "          \"id\": 21031067,\n" +
                "          \"node_id\": \"MDQ6VXNlcjIxMDMxMDY3\",\n" +
                "          \"avatar_url\": \"https://avatars1.githubusercontent.com/u/21031067?v=4\",\n" +
                "          \"gravatar_id\": \"\",\n" +
                "          \"url\": \"https://api.github.com/users/Codertocat\",\n" +
                "          \"html_url\": \"https://github.com/Codertocat\",\n" +
                "          \"followers_url\": \"https://api.github.com/users/Codertocat/followers\",\n" +
                "          \"following_url\": \"https://api.github.com/users/Codertocat/following{/other_user}\",\n" +
                "          \"gists_url\": \"https://api.github.com/users/Codertocat/gists{/gist_id}\",\n" +
                "          \"starred_url\": \"https://api.github.com/users/Codertocat/starred{/owner}{/repo}\",\n" +
                "          \"subscriptions_url\": \"https://api.github.com/users/Codertocat/subscriptions\",\n" +
                "          \"organizations_url\": \"https://api.github.com/users/Codertocat/orgs\",\n" +
                "          \"repos_url\": \"https://api.github.com/users/Codertocat/repos\",\n" +
                "          \"events_url\": \"https://api.github.com/users/Codertocat/events{/privacy}\",\n" +
                "          \"received_events_url\": \"https://api.github.com/users/Codertocat/received_events\",\n" +
                "          \"type\": \"User\",\n" +
                "          \"site_admin\": false\n" +
                "        },\n" +
                "        \"html_url\": \"https://github.com/Codertocat/Hello-World\",\n" +
                "        \"description\": null,\n" +
                "        \"fork\": false,\n" +
                "        \"url\": \"https://api.github.com/repos/Codertocat/Hello-World\",\n" +
                "        \"forks_url\": \"https://api.github.com/repos/Codertocat/Hello-World/forks\",\n" +
                "        \"keys_url\": \"https://api.github.com/repos/Codertocat/Hello-World/keys{/key_id}\",\n" +
                "        \"collaborators_url\": \"https://api.github.com/repos/Codertocat/Hello-World/collaborators{/collaborator}\",\n" +
                "        \"teams_url\": \"https://api.github.com/repos/Codertocat/Hello-World/teams\",\n" +
                "        \"hooks_url\": \"https://api.github.com/repos/Codertocat/Hello-World/hooks\",\n" +
                "        \"issue_events_url\": \"https://api.github.com/repos/Codertocat/Hello-World/issues/events{/number}\",\n" +
                "        \"events_url\": \"https://api.github.com/repos/Codertocat/Hello-World/events\",\n" +
                "        \"assignees_url\": \"https://api.github.com/repos/Codertocat/Hello-World/assignees{/user}\",\n" +
                "        \"branches_url\": \"https://api.github.com/repos/Codertocat/Hello-World/branches{/branch}\",\n" +
                "        \"tags_url\": \"https://api.github.com/repos/Codertocat/Hello-World/tags\",\n" +
                "        \"blobs_url\": \"https://api.github.com/repos/Codertocat/Hello-World/git/blobs{/sha}\",\n" +
                "        \"git_tags_url\": \"https://api.github.com/repos/Codertocat/Hello-World/git/tags{/sha}\",\n" +
                "        \"git_refs_url\": \"https://api.github.com/repos/Codertocat/Hello-World/git/refs{/sha}\",\n" +
                "        \"trees_url\": \"https://api.github.com/repos/Codertocat/Hello-World/git/trees{/sha}\",\n" +
                "        \"statuses_url\": \"https://api.github.com/repos/Codertocat/Hello-World/statuses/{sha}\",\n" +
                "        \"languages_url\": \"https://api.github.com/repos/Codertocat/Hello-World/languages\",\n" +
                "        \"stargazers_url\": \"https://api.github.com/repos/Codertocat/Hello-World/stargazers\",\n" +
                "        \"contributors_url\": \"https://api.github.com/repos/Codertocat/Hello-World/contributors\",\n" +
                "        \"subscribers_url\": \"https://api.github.com/repos/Codertocat/Hello-World/subscribers\",\n" +
                "        \"subscription_url\": \"https://api.github.com/repos/Codertocat/Hello-World/subscription\",\n" +
                "        \"commits_url\": \"https://api.github.com/repos/Codertocat/Hello-World/commits{/sha}\",\n" +
                "        \"git_commits_url\": \"https://api.github.com/repos/Codertocat/Hello-World/git/commits{/sha}\",\n" +
                "        \"comments_url\": \"https://api.github.com/repos/Codertocat/Hello-World/comments{/number}\",\n" +
                "        \"issue_comment_url\": \"https://api.github.com/repos/Codertocat/Hello-World/issues/comments{/number}\",\n" +
                "        \"contents_url\": \"https://api.github.com/repos/Codertocat/Hello-World/contents/{+path}\",\n" +
                "        \"compare_url\": \"https://api.github.com/repos/Codertocat/Hello-World/compare/{base}...{head}\",\n" +
                "        \"merges_url\": \"https://api.github.com/repos/Codertocat/Hello-World/merges\",\n" +
                "        \"archive_url\": \"https://api.github.com/repos/Codertocat/Hello-World/{archive_format}{/ref}\",\n" +
                "        \"downloads_url\": \"https://api.github.com/repos/Codertocat/Hello-World/downloads\",\n" +
                "        \"issues_url\": \"https://api.github.com/repos/Codertocat/Hello-World/issues{/number}\",\n" +
                "        \"pulls_url\": \"https://api.github.com/repos/Codertocat/Hello-World/pulls{/number}\",\n" +
                "        \"milestones_url\": \"https://api.github.com/repos/Codertocat/Hello-World/milestones{/number}\",\n" +
                "        \"notifications_url\": \"https://api.github.com/repos/Codertocat/Hello-World/notifications{?since,all,participating}\",\n" +
                "        \"labels_url\": \"https://api.github.com/repos/Codertocat/Hello-World/labels{/name}\",\n" +
                "        \"releases_url\": \"https://api.github.com/repos/Codertocat/Hello-World/releases{/id}\",\n" +
                "        \"deployments_url\": \"https://api.github.com/repos/Codertocat/Hello-World/deployments\",\n" +
                "        \"created_at\": \"2019-05-15T15:19:25Z\",\n" +
                "        \"updated_at\": \"2019-05-15T15:19:27Z\",\n" +
                "        \"pushed_at\": \"2019-05-15T15:20:32Z\",\n" +
                "        \"git_url\": \"git://github.com/Codertocat/Hello-World.git\",\n" +
                "        \"ssh_url\": \"git@github.com:Codertocat/Hello-World.git\",\n" +
                "        \"clone_url\": \"https://github.com/Codertocat/Hello-World.git\",\n" +
                "        \"svn_url\": \"https://github.com/Codertocat/Hello-World\",\n" +
                "        \"homepage\": null,\n" +
                "        \"size\": 0,\n" +
                "        \"stargazers_count\": 0,\n" +
                "        \"watchers_count\": 0,\n" +
                "        \"language\": null,\n" +
                "        \"has_issues\": true,\n" +
                "        \"has_projects\": true,\n" +
                "        \"has_downloads\": true,\n" +
                "        \"has_wiki\": true,\n" +
                "        \"has_pages\": true,\n" +
                "        \"forks_count\": 0,\n" +
                "        \"mirror_url\": null,\n" +
                "        \"archived\": false,\n" +
                "        \"disabled\": false,\n" +
                "        \"open_issues_count\": 2,\n" +
                "        \"license\": null,\n" +
                "        \"forks\": 0,\n" +
                "        \"open_issues\": 2,\n" +
                "        \"watchers\": 0,\n" +
                "        \"default_branch\": \"master\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"base\": {\n" +
                "      \"label\": \"Codertocat:master\",\n" +
                "      \"ref\": \"master\",\n" +
                "      \"sha\": \"f95f852bd8fca8fcc58a9a2d6c842781e32a215e\",\n" +
                "      \"user\": {\n" +
                "        \"login\": \"Codertocat\",\n" +
                "        \"id\": 21031067,\n" +
                "        \"node_id\": \"MDQ6VXNlcjIxMDMxMDY3\",\n" +
                "        \"avatar_url\": \"https://avatars1.githubusercontent.com/u/21031067?v=4\",\n" +
                "        \"gravatar_id\": \"\",\n" +
                "        \"url\": \"https://api.github.com/users/Codertocat\",\n" +
                "        \"html_url\": \"https://github.com/Codertocat\",\n" +
                "        \"followers_url\": \"https://api.github.com/users/Codertocat/followers\",\n" +
                "        \"following_url\": \"https://api.github.com/users/Codertocat/following{/other_user}\",\n" +
                "        \"gists_url\": \"https://api.github.com/users/Codertocat/gists{/gist_id}\",\n" +
                "        \"starred_url\": \"https://api.github.com/users/Codertocat/starred{/owner}{/repo}\",\n" +
                "        \"subscriptions_url\": \"https://api.github.com/users/Codertocat/subscriptions\",\n" +
                "        \"organizations_url\": \"https://api.github.com/users/Codertocat/orgs\",\n" +
                "        \"repos_url\": \"https://api.github.com/users/Codertocat/repos\",\n" +
                "        \"events_url\": \"https://api.github.com/users/Codertocat/events{/privacy}\",\n" +
                "        \"received_events_url\": \"https://api.github.com/users/Codertocat/received_events\",\n" +
                "        \"type\": \"User\",\n" +
                "        \"site_admin\": false\n" +
                "      },\n" +
                "      \"repo\": {\n" +
                "        \"id\": 186853002,\n" +
                "        \"node_id\": \"MDEwOlJlcG9zaXRvcnkxODY4NTMwMDI=\",\n" +
                "        \"name\": \"Hello-World\",\n" +
                "        \"full_name\": \"Codertocat/Hello-World\",\n" +
                "        \"private\": false,\n" +
                "        \"owner\": {\n" +
                "          \"login\": \"Codertocat\",\n" +
                "          \"id\": 21031067,\n" +
                "          \"node_id\": \"MDQ6VXNlcjIxMDMxMDY3\",\n" +
                "          \"avatar_url\": \"https://avatars1.githubusercontent.com/u/21031067?v=4\",\n" +
                "          \"gravatar_id\": \"\",\n" +
                "          \"url\": \"https://api.github.com/users/Codertocat\",\n" +
                "          \"html_url\": \"https://github.com/Codertocat\",\n" +
                "          \"followers_url\": \"https://api.github.com/users/Codertocat/followers\",\n" +
                "          \"following_url\": \"https://api.github.com/users/Codertocat/following{/other_user}\",\n" +
                "          \"gists_url\": \"https://api.github.com/users/Codertocat/gists{/gist_id}\",\n" +
                "          \"starred_url\": \"https://api.github.com/users/Codertocat/starred{/owner}{/repo}\",\n" +
                "          \"subscriptions_url\": \"https://api.github.com/users/Codertocat/subscriptions\",\n" +
                "          \"organizations_url\": \"https://api.github.com/users/Codertocat/orgs\",\n" +
                "          \"repos_url\": \"https://api.github.com/users/Codertocat/repos\",\n" +
                "          \"events_url\": \"https://api.github.com/users/Codertocat/events{/privacy}\",\n" +
                "          \"received_events_url\": \"https://api.github.com/users/Codertocat/received_events\",\n" +
                "          \"type\": \"User\",\n" +
                "          \"site_admin\": false\n" +
                "        },\n" +
                "        \"html_url\": \"https://github.com/Codertocat/Hello-World\",\n" +
                "        \"description\": null,\n" +
                "        \"fork\": false,\n" +
                "        \"url\": \"https://api.github.com/repos/Codertocat/Hello-World\",\n" +
                "        \"forks_url\": \"https://api.github.com/repos/Codertocat/Hello-World/forks\",\n" +
                "        \"keys_url\": \"https://api.github.com/repos/Codertocat/Hello-World/keys{/key_id}\",\n" +
                "        \"collaborators_url\": \"https://api.github.com/repos/Codertocat/Hello-World/collaborators{/collaborator}\",\n" +
                "        \"teams_url\": \"https://api.github.com/repos/Codertocat/Hello-World/teams\",\n" +
                "        \"hooks_url\": \"https://api.github.com/repos/Codertocat/Hello-World/hooks\",\n" +
                "        \"issue_events_url\": \"https://api.github.com/repos/Codertocat/Hello-World/issues/events{/number}\",\n" +
                "        \"events_url\": \"https://api.github.com/repos/Codertocat/Hello-World/events\",\n" +
                "        \"assignees_url\": \"https://api.github.com/repos/Codertocat/Hello-World/assignees{/user}\",\n" +
                "        \"branches_url\": \"https://api.github.com/repos/Codertocat/Hello-World/branches{/branch}\",\n" +
                "        \"tags_url\": \"https://api.github.com/repos/Codertocat/Hello-World/tags\",\n" +
                "        \"blobs_url\": \"https://api.github.com/repos/Codertocat/Hello-World/git/blobs{/sha}\",\n" +
                "        \"git_tags_url\": \"https://api.github.com/repos/Codertocat/Hello-World/git/tags{/sha}\",\n" +
                "        \"git_refs_url\": \"https://api.github.com/repos/Codertocat/Hello-World/git/refs{/sha}\",\n" +
                "        \"trees_url\": \"https://api.github.com/repos/Codertocat/Hello-World/git/trees{/sha}\",\n" +
                "        \"statuses_url\": \"https://api.github.com/repos/Codertocat/Hello-World/statuses/{sha}\",\n" +
                "        \"languages_url\": \"https://api.github.com/repos/Codertocat/Hello-World/languages\",\n" +
                "        \"stargazers_url\": \"https://api.github.com/repos/Codertocat/Hello-World/stargazers\",\n" +
                "        \"contributors_url\": \"https://api.github.com/repos/Codertocat/Hello-World/contributors\",\n" +
                "        \"subscribers_url\": \"https://api.github.com/repos/Codertocat/Hello-World/subscribers\",\n" +
                "        \"subscription_url\": \"https://api.github.com/repos/Codertocat/Hello-World/subscription\",\n" +
                "        \"commits_url\": \"https://api.github.com/repos/Codertocat/Hello-World/commits{/sha}\",\n" +
                "        \"git_commits_url\": \"https://api.github.com/repos/Codertocat/Hello-World/git/commits{/sha}\",\n" +
                "        \"comments_url\": \"https://api.github.com/repos/Codertocat/Hello-World/comments{/number}\",\n" +
                "        \"issue_comment_url\": \"https://api.github.com/repos/Codertocat/Hello-World/issues/comments{/number}\",\n" +
                "        \"contents_url\": \"https://api.github.com/repos/Codertocat/Hello-World/contents/{+path}\",\n" +
                "        \"compare_url\": \"https://api.github.com/repos/Codertocat/Hello-World/compare/{base}...{head}\",\n" +
                "        \"merges_url\": \"https://api.github.com/repos/Codertocat/Hello-World/merges\",\n" +
                "        \"archive_url\": \"https://api.github.com/repos/Codertocat/Hello-World/{archive_format}{/ref}\",\n" +
                "        \"downloads_url\": \"https://api.github.com/repos/Codertocat/Hello-World/downloads\",\n" +
                "        \"issues_url\": \"https://api.github.com/repos/Codertocat/Hello-World/issues{/number}\",\n" +
                "        \"pulls_url\": \"https://api.github.com/repos/Codertocat/Hello-World/pulls{/number}\",\n" +
                "        \"milestones_url\": \"https://api.github.com/repos/Codertocat/Hello-World/milestones{/number}\",\n" +
                "        \"notifications_url\": \"https://api.github.com/repos/Codertocat/Hello-World/notifications{?since,all,participating}\",\n" +
                "        \"labels_url\": \"https://api.github.com/repos/Codertocat/Hello-World/labels{/name}\",\n" +
                "        \"releases_url\": \"https://api.github.com/repos/Codertocat/Hello-World/releases{/id}\",\n" +
                "        \"deployments_url\": \"https://api.github.com/repos/Codertocat/Hello-World/deployments\",\n" +
                "        \"created_at\": \"2019-05-15T15:19:25Z\",\n" +
                "        \"updated_at\": \"2019-05-15T15:19:27Z\",\n" +
                "        \"pushed_at\": \"2019-05-15T15:20:32Z\",\n" +
                "        \"git_url\": \"git://github.com/Codertocat/Hello-World.git\",\n" +
                "        \"ssh_url\": \"git@github.com:Codertocat/Hello-World.git\",\n" +
                "        \"clone_url\": \"https://github.com/Codertocat/Hello-World.git\",\n" +
                "        \"svn_url\": \"https://github.com/Codertocat/Hello-World\",\n" +
                "        \"homepage\": null,\n" +
                "        \"size\": 0,\n" +
                "        \"stargazers_count\": 0,\n" +
                "        \"watchers_count\": 0,\n" +
                "        \"language\": null,\n" +
                "        \"has_issues\": true,\n" +
                "        \"has_projects\": true,\n" +
                "        \"has_downloads\": true,\n" +
                "        \"has_wiki\": true,\n" +
                "        \"has_pages\": true,\n" +
                "        \"forks_count\": 0,\n" +
                "        \"mirror_url\": null,\n" +
                "        \"archived\": false,\n" +
                "        \"disabled\": false,\n" +
                "        \"open_issues_count\": 2,\n" +
                "        \"license\": null,\n" +
                "        \"forks\": 0,\n" +
                "        \"open_issues\": 2,\n" +
                "        \"watchers\": 0,\n" +
                "        \"default_branch\": \"master\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"_links\": {\n" +
                "      \"self\": {\n" +
                "        \"href\": \"https://api.github.com/repos/Codertocat/Hello-World/pulls/2\"\n" +
                "      },\n" +
                "      \"html\": {\n" +
                "        \"href\": \"https://github.com/Codertocat/Hello-World/pull/2\"\n" +
                "      },\n" +
                "      \"issue\": {\n" +
                "        \"href\": \"https://api.github.com/repos/Codertocat/Hello-World/issues/2\"\n" +
                "      },\n" +
                "      \"comments\": {\n" +
                "        \"href\": \"https://api.github.com/repos/Codertocat/Hello-World/issues/2/comments\"\n" +
                "      },\n" +
                "      \"review_comments\": {\n" +
                "        \"href\": \"https://api.github.com/repos/Codertocat/Hello-World/pulls/2/comments\"\n" +
                "      },\n" +
                "      \"review_comment\": {\n" +
                "        \"href\": \"https://api.github.com/repos/Codertocat/Hello-World/pulls/comments{/number}\"\n" +
                "      },\n" +
                "      \"commits\": {\n" +
                "        \"href\": \"https://api.github.com/repos/Codertocat/Hello-World/pulls/2/commits\"\n" +
                "      },\n" +
                "      \"statuses\": {\n" +
                "        \"href\": \"https://api.github.com/repos/Codertocat/Hello-World/statuses/ec26c3e57ca3a959ca5aad62de7213c562f8c821\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"author_association\": \"OWNER\",\n" +
                "    \"draft\": false,\n" +
                "    \"merged\": false,\n" +
                "    \"mergeable\": null,\n" +
                "    \"rebaseable\": null,\n" +
                "    \"mergeable_state\": \"unknown\",\n" +
                "    \"merged_by\": null,\n" +
                "    \"comments\": 0,\n" +
                "    \"review_comments\": 0,\n" +
                "    \"maintainer_can_modify\": false,\n" +
                "    \"commits\": 1,\n" +
                "    \"additions\": 1,\n" +
                "    \"deletions\": 1,\n" +
                "    \"changed_files\": 1\n" +
                "  },\n" +
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
                "    \"updated_at\": \"2019-05-15T15:19:27Z\",\n" +
                "    \"pushed_at\": \"2019-05-15T15:20:32Z\",\n" +
                "    \"git_url\": \"git://github.com/Codertocat/Hello-World.git\",\n" +
                "    \"ssh_url\": \"git@github.com:Codertocat/Hello-World.git\",\n" +
                "    \"clone_url\": \"https://github.com/Codertocat/Hello-World.git\",\n" +
                "    \"svn_url\": \"https://github.com/Codertocat/Hello-World\",\n" +
                "    \"homepage\": null,\n" +
                "    \"size\": 0,\n" +
                "    \"stargazers_count\": 0,\n" +
                "    \"watchers_count\": 0,\n" +
                "    \"language\": null,\n" +
                "    \"has_issues\": true,\n" +
                "    \"has_projects\": true,\n" +
                "    \"has_downloads\": true,\n" +
                "    \"has_wiki\": true,\n" +
                "    \"has_pages\": true,\n" +
                "    \"forks_count\": 0,\n" +
                "    \"mirror_url\": null,\n" +
                "    \"archived\": false,\n" +
                "    \"disabled\": false,\n" +
                "    \"open_issues_count\": 2,\n" +
                "    \"license\": null,\n" +
                "    \"forks\": 0,\n" +
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
