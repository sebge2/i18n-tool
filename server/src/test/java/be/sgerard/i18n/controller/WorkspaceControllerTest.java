package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryDto;
import be.sgerard.i18n.model.repository.dto.GitRepositoryDto;
import be.sgerard.i18n.model.workspace.WorkspaceDto;
import be.sgerard.test.i18n.support.GitHubTest;
import be.sgerard.test.i18n.support.WithInternalUser;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolLocalRepositoryCreationDto;
import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolRepositoryCreationDto;
import static be.sgerard.test.i18n.model.UserDtoTestUtils.userJohnDoeCreation;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
public class WorkspaceControllerTest extends AbstractControllerTest {

    @Before
    public void setup() throws Exception {
        user.createUser(userJohnDoeCreation().build());
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    @GitHubTest
    public void synchronizeGitHubRepository() throws Exception {
        final GitHubRepositoryDto repository = this.repository.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).initialize().get();

        asyncMvc
                .perform(post("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", repository.getId()))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void synchronizeGitRepository() throws Exception {
        final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).initialize().get();

        asyncMvc
                .perform(post("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", repository.getId()))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    @GitHubTest
    public void initializeGitHubRepository() throws Exception {
        final WorkspaceDto masterWorkspace = repository
                .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                .initialize()
                .workspaces()
                .sync()
                .getOrDie("master");

        asyncMvc
                .perform(post("/api/workspace/{id}/do?action=INITIALIZE", masterWorkspace.getId()))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    @GitHubTest
    public void initializeGitRepository() throws Exception {
        final WorkspaceDto masterWorkspace = repository
                .create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class)
                .initialize()
                .workspaces()
                .sync()
                .getOrDie("master");

        asyncMvc
                .perform(post("/api/workspace/{id}/do?action=INITIALIZE", masterWorkspace.getId()))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk());
    }

    // TODO findall
    // TODO findall of repo
    // TODO get by id
    // TODO delete
    // TODO publish

}
