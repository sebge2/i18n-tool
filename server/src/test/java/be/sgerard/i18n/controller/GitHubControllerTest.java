package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryDto;
import be.sgerard.i18n.model.workspace.WorkspaceDto;
import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import be.sgerard.i18n.service.github.GitHubWebHookService;
import be.sgerard.i18n.service.github.external.GitHubEventType;
import be.sgerard.test.i18n.support.WithInternalUser;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static be.sgerard.test.i18n.model.GitHubBranchCreatedEventDtoTestUtils.i18nToolRelease20205BranchCreatedEvent;
import static be.sgerard.test.i18n.model.GitHubBranchDeletedEventDtoTestUtils.i18nToolRelease20204BranchDeletedEvent;
import static be.sgerard.test.i18n.model.GitHubBranchPullRequestEventDtoTestUtils.i18nToolRelease20206PullRequestEvent;
import static be.sgerard.test.i18n.model.GitHubRepositoryPatchDtoTestUtils.WEB_HOOK_SECRET;
import static be.sgerard.test.i18n.model.GitHubRepositoryPatchDtoTestUtils.i18nToolRepositoryPatchDto;
import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolRepositoryCreationDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Sebastien Gerard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GitHubControllerTest extends AbstractControllerTest {

    @BeforeAll
    public void setupRepo() throws Exception {
        gitRepo
                .createMockFor(i18nToolRepositoryCreationDto())
                .allowAnonymousRead()
                .onCurrentGitProject()
                .create()
                .createBranches("develop", "release/2020.6", "release/2020.4");
    }

    @AfterAll
    public void destroy() {
        gitRepo.destroyAll();
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void deletedBranchEvent() throws Exception {
        repository
                .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                .hint("my-repo")
                .update(i18nToolRepositoryPatchDto())
                .initialize()
                .workspaces()
                .sync();

        gitRepo
                .getRepo(i18nToolRepositoryCreationDto())
                .deleteBranches("release/2020.4");

        assertThat(getWorkspacesForRepo())
                .extracting(WorkspaceDto::getBranch)
                .contains("release/2020.4");

        asyncMvc
                .perform(postRequest(i18nToolRelease20204BranchDeletedEvent(), GitHubEventType.BRANCH_DELETED, WEB_HOOK_SECRET))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(content().string("Signature Verified.\nReceived 155 bytes."));

        assertThat(getWorkspacesForRepo())
                .extracting(WorkspaceDto::getBranch)
                .doesNotContain("release/2020.4");
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void createdBranchEvent() throws Exception {
        repository
                .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                .hint("my-repo")
                .update(i18nToolRepositoryPatchDto())
                .initialize()
                .workspaces()
                .sync();

        gitRepo
                .getRepo(i18nToolRepositoryCreationDto())
                .createBranches("release/2020.5");

        asyncMvc
                .perform(postRequest(i18nToolRelease20205BranchCreatedEvent(), GitHubEventType.BRANCH_CREATED, WEB_HOOK_SECRET))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(content().string("Signature Verified.\nReceived 155 bytes."));

        assertThat(getWorkspacesForRepo())
                .extracting(WorkspaceDto::getBranch)
                .contains("release/2020.5");
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void pullRequestEvent() throws Exception {
        repository
                .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                .hint("my-repo")
                .update(i18nToolRepositoryPatchDto())
                .initialize()
                .workspaces()
                .sync()
                .workspaceForBranch("release/2020.6")
                .initialize()
                .publish("test");

        asyncMvc
                .perform(postRequest(i18nToolRelease20206PullRequestEvent(), GitHubEventType.PULL_REQUEST, WEB_HOOK_SECRET))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(content().string("Signature Verified.\nReceived 180 bytes."));

        assertThat(getWorkspacesForRepo())
                .extracting(WorkspaceDto::getStatus)
                .containsOnly(WorkspaceStatus.NOT_INITIALIZED, WorkspaceStatus.NOT_INITIALIZED, WorkspaceStatus.NOT_INITIALIZED);
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void wrongCredentials() throws Exception {
        repository
                .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                .update(i18nToolRepositoryPatchDto())
                .initialize();

        asyncMvc
                .perform(postRequest(i18nToolRelease20204BranchDeletedEvent(), GitHubEventType.BRANCH_DELETED, WEB_HOOK_SECRET + "wrong"))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.messages[0]").value("The signature [sha1=fb901085c0f6f1ee6dcc7aa6771f7f9d5452f96e] is invalid."));
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void noCredentialsNeeded() throws Exception {
        repository
                .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                .initialize();

        asyncMvc
                .perform(postRequest(i18nToolRelease20204BranchDeletedEvent(), GitHubEventType.BRANCH_DELETED, WEB_HOOK_SECRET + "wrong"))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk());
    }

    private Collection<WorkspaceDto> getWorkspacesForRepo() throws Exception {
        return repository
                .forHint("my-repo")
                .initialize()
                .workspaces()
                .get();
    }

    @SuppressWarnings("SameParameterValue")
    private MockHttpServletRequestBuilder postRequest(Object payload, GitHubEventType eventType, String webHookSecret) throws Exception {
        final String content = objectMapper.writeValueAsString(payload);

        return post("/api/git-hub/event")
                .header(HttpHeaders.USER_AGENT, "GitHub-Hookshot/test")
                .header(GitHubWebHookService.EVENT_TYPE, eventType.getType())
                .header(GitHubWebHookService.SIGNATURE, String.format("sha1=%s", new HmacUtils(HmacAlgorithms.HMAC_SHA_1, webHookSecret).hmacHex(content)))
                .content(content)
                .contentType(MediaType.APPLICATION_JSON);
    }
}
