package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryDto;
import be.sgerard.i18n.service.github.GitHubWebHookService;
import be.sgerard.i18n.service.github.external.GitHubEventType;
import be.sgerard.test.i18n.support.WithInternalUser;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static be.sgerard.test.i18n.model.GitHubBranchCreatedEventDtoTestUtils.i18nToolRelease20204BranchCreatedEvent;
import static be.sgerard.test.i18n.model.GitHubBranchDeletedEventDtoTestUtils.i18nToolRelease20204BranchDeletedEvent;
import static be.sgerard.test.i18n.model.GitHubBranchPullRequestEventDtoTestUtils.i18nToolRelease20204PullRequestEvent;
import static be.sgerard.test.i18n.model.GitHubRepositoryPatchDtoTestUtils.i18nToolRepositoryPatchDto;
import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolRepositoryCreationDto;
import static be.sgerard.test.i18n.model.UserDtoTestUtils.userJohnDoeCreation;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
public class GitHubControllerTest extends AbstractControllerTest {

    @Before
    public void setup() throws Exception {
        user.createUser(userJohnDoeCreation().build());
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void noUserCredentials() throws Exception {
        repository
                .create(i18nToolRepositoryCreationDto())
                .update(i18nToolRepositoryPatchDto())
                .initialize();

        asyncMvc
                .perform(
                        post("/api/git-hub/event")
                                .header(HttpHeaders.USER_AGENT, "GitHub-Hookshot/test")
                                .header(GitHubWebHookService.EVENT_TYPE, GitHubEventType.BRANCH_CREATED.getType())
                                .content(objectMapper.writeValueAsString(i18nToolRelease20204BranchCreatedEvent()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isUnauthorized());
    }

    // TODO test user credentials
    // TODO test type not supported
    // TODO test signature .header(GitHubWebHookService.SIGNATURE, "x")

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void createdBranchEvent() throws Exception {
        repository
                .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                .update(i18nToolRepositoryPatchDto())
                .initialize();

        asyncMvc
                .perform(
                        post("/api/git-hub/event")
                                .header(HttpHeaders.USER_AGENT, "GitHub-Hookshot/test")
                                .header(GitHubWebHookService.EVENT_TYPE, GitHubEventType.BRANCH_CREATED.getType())
                                .content(objectMapper.writeValueAsString(i18nToolRelease20204BranchCreatedEvent()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(content().string("Signature Verified.\nReceived 155 bytes."));
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void deletedBranchEvent() throws Exception {
        repository
                .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                .update(i18nToolRepositoryPatchDto())
                .initialize();

        asyncMvc
                .perform(
                        post("/api/git-hub/event")
                                .header(HttpHeaders.USER_AGENT, "GitHub-Hookshot/test")
                                .header(GitHubWebHookService.EVENT_TYPE, GitHubEventType.BRANCH_CREATED.getType())
                                .content(objectMapper.writeValueAsString(i18nToolRelease20204BranchDeletedEvent()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(content().string("Signature Verified.\nReceived 155 bytes."));
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void pullRequestEvent() throws Exception {
        repository
                .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                .update(i18nToolRepositoryPatchDto())
                .initialize();

        asyncMvc
                .perform(
                        post("/api/git-hub/event")
                                .header(HttpHeaders.USER_AGENT, "GitHub-Hookshot/test")
                                .header(GitHubWebHookService.EVENT_TYPE, GitHubEventType.BRANCH_CREATED.getType())
                                .content(objectMapper.writeValueAsString(i18nToolRelease20204PullRequestEvent()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(content().string("Signature Verified.\nReceived 155 bytes."));
    }
}
