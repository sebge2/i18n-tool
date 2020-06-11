package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryDto;
import be.sgerard.i18n.model.repository.dto.GitRepositoryDto;
import be.sgerard.i18n.model.workspace.WorkspaceDto;
import be.sgerard.test.i18n.support.WithInternalUser;
import org.junit.jupiter.api.*;
import org.springframework.transaction.annotation.Transactional;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolLocalRepositoryCreationDto;
import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolRepositoryCreationDto;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkspaceControllerTest extends AbstractControllerTest {

    @BeforeAll
    public void setupRepo() throws Exception {
        gitRepo
                .createMockFor(i18nToolRepositoryCreationDto())
                .allowAnonymousRead()
                .baseOnCurrentGitProject()
                .create()
                .createBranches("release/2020.05", "release/2020.06");

        gitRepo
                .createMockFor(i18nToolLocalRepositoryCreationDto())
                .allowAnonymousRead()
                .baseOnCurrentGitProject()
                .create()
                .createBranches("release/2020.05", "release/2020.06");
    }

    @AfterAll
    public void destroy() {
        gitRepo.destroyAll();
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findAll() throws Exception {
        final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).initialize().get();

        asyncMvc
                .perform(post("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", repository.getId()))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk());

        asyncMvc
                .perform(get("/api/repository/workspace"))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findAllOfRepository() throws Exception {
        final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).initialize().get();

        asyncMvc
                .perform(post("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", repository.getId()))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk());

        asyncMvc
                .perform(get("/api/repository/{id}/workspace", repository.getId()))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Nested
    @DisplayName("GitHub")
    class GitHub extends AbstractControllerTest {

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void synchronize() throws Exception {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).initialize().get();

            asyncMvc
                    .perform(post("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void initialize() throws Exception {
            final WorkspaceDto masterWorkspace = repository
                    .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                    .initialize()
                    .workspaces()
                    .sync()
                    .getOrDie("master");

            asyncMvc
                    .perform(post("/api/repository/workspace/{id}/do?action=INITIALIZE", masterWorkspace.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk());
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void deleteWorkspace() throws Exception {
            final WorkspaceDto masterWorkspace = repository
                    .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                    .initialize()
                    .workspaces()
                    .sync()
                    .getOrDie("master");

            asyncMvc
                    .perform(post("/api/repository/workspace/{id}/do?action=INITIALIZE", masterWorkspace.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk());

            asyncMvc
                    .perform(delete("/api/repository/workspace/{id}", masterWorkspace.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("Git")
    class Git extends AbstractControllerTest {

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void synchronize() throws Exception {
            final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).initialize().get();

            asyncMvc
                    .perform(post("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void initialize() throws Exception {
            final WorkspaceDto masterWorkspace = repository
                    .create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class)
                    .initialize()
                    .workspaces()
                    .sync()
                    .getOrDie("master");

            asyncMvc
                    .perform(post("/api/repository/workspace/{id}/do?action=INITIALIZE", masterWorkspace.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk());
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void deleteWorkspace() throws Exception {
            final WorkspaceDto masterWorkspace = repository
                    .create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class)
                    .initialize()
                    .workspaces()
                    .sync()
                    .getOrDie("master");

            asyncMvc
                    .perform(post("/api/repository/workspace/{id}/do?action=INITIALIZE", masterWorkspace.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk());

            asyncMvc
                    .perform(delete("/api/repository/workspace/{id}", masterWorkspace.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isNoContent());
        }
    }

    // TODO get by id
    // TODO publish

}
