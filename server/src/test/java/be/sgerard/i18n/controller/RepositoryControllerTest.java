package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.dto.*;
import be.sgerard.test.i18n.support.WithInternalUser;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RepositoryControllerTest extends AbstractControllerTest {

    @BeforeAll
    public void setupRepo() throws Exception {
        gitRepo
                .createMockFor(i18nToolRepositoryCreationDto())
                .allowAnonymousRead()
                .onCurrentGitProject()
                .create();
        gitRepo
                .createMockFor(i18nToolLocalRepositoryCreationDto())
                .allowAnonymousRead()
                .onCurrentGitProject()
                .create();
        gitRepo
                .createMockFor(privateI18nToolRepositoryCreationDto())
                .userKey("ABCD")
                .onCurrentGitProject()
                .create();
    }

    @AfterAll
    public void destroy() {
        gitRepo.destroyAll();
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findAll() throws Exception {
        final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

        asyncMvc
                .perform(get("/api/repository"))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.id=='" + repository.getId() + "')]").exists());
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findById() throws Exception {
        final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

        asyncMvc
                .perform(get("/api/repository/{id}", repository.getId()))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(RepositoryStatus.NOT_INITIALIZED.name()))
                .andExpect(jsonPath("$.name").value(repository.getName()))
                .andExpect(jsonPath("$.location").value(repository.getLocation()));
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findByIdNotFound() throws Exception {
        repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class);

        asyncMvc
                .perform(get("/api/repository/{id}", "unknown"))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isNotFound());
    }

    @Nested
    @DisplayName("GitHub")
    class GitHub extends AbstractControllerTest {

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void create() throws Exception {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            asyncMvc
                    .perform(
                            post("/api/repository")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(creationDto))
                    )
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(RepositoryStatus.NOT_INITIALIZED.name()))
                    .andExpect(jsonPath("$.name").value("sebge2/i18n-tool"))
                    .andExpect(jsonPath("$.location").value("https://github.com/sebge2/i18n-tool.git"));
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void createWrongUrl() throws Exception {
            final GitHubRepositoryCreationDto creationDto = new GitHubRepositoryCreationDto("unknown", "unknown", null);

            asyncMvc
                    .perform(
                            post("/api/repository")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(creationDto))
                    )
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.messages[0]").value("The Git repository [https://github.com/unknown/unknown.git] has not been found. Please verify the URL."));
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void createInvalidAccessKeyCredentials() throws Exception {
            final GitHubRepositoryCreationDto creationDto = new GitHubRepositoryCreationDto("sebge2", "private-i18n-tool", "ZEF");

            asyncMvc
                    .perform(
                            post("/api/repository")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(creationDto))
                    )
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.messages[0]").value("Please verify your credentials for accessing the Git repository [https://github.com/sebge2/private-i18n-tool.git]."));
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void createNoAccessKeyCredentials() throws Exception {
            final GitHubRepositoryCreationDto creationDto = new GitHubRepositoryCreationDto("sebge2", "private-i18n-tool", null);

            asyncMvc
                    .perform(
                            post("/api/repository")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(creationDto))
                    )
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.messages[0]").value("Please verify your credentials for accessing the Git repository [https://github.com/sebge2/private-i18n-tool.git]."));
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void createValidAccessKeyCredentials() throws Exception {
            final GitHubRepositoryCreationDto creationDto = privateI18nToolRepositoryCreationDto();

            asyncMvc
                    .perform(
                            post("/api/repository")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(creationDto))
                    )
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isCreated());
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void createSameName() throws Exception {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            asyncMvc
                    .perform(
                            post("/api/repository")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(creationDto))
                    )
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isCreated());

            asyncMvc
                    .perform(
                            post("/api/repository")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(creationDto))
                    )
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.messages[0]").value("Another repository is already named [sebge2/i18n-tool]. Names must be unique."));
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void initialize() throws Exception {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            asyncMvc
                    .perform(post("/api/repository/{id}/do?action=INITIALIZE", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(RepositoryStatus.INITIALIZED.name()));
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void initializeTwice() throws Exception {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            asyncMvc
                    .perform(post("/api/repository/{id}/do?action=INITIALIZE", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(RepositoryStatus.INITIALIZED.name()));

            asyncMvc
                    .perform(post("/api/repository/{id}/do?action=INITIALIZE", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(RepositoryStatus.INITIALIZED.name()));
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void update() throws Exception {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            final GitHubRepositoryPatchDto patchDto = GitHubRepositoryPatchDto.gitHubBuilder()
                    .id(repository.getId())
                    .webHookSecret("a secret")
                    .accessKey("an access key")
                    .build();

            asyncMvc
                    .perform(
                            patch("/api/repository/{id}", repository.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(patchDto))
                    )
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessKey").value("an access key"))
                    .andExpect(jsonPath("$.webHookSecret").value("a secret"));
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void deleteRepository() throws Exception {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            asyncMvc
                    .perform(delete("/api/repository/{id}", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isNoContent());

            asyncMvc
                    .perform(get("/api/repository/{id}", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Git")
    class Git extends AbstractControllerTest {

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void create() throws Exception {
            final GitRepositoryCreationDto creationDto = i18nToolLocalRepositoryCreationDto();

            asyncMvc
                    .perform(
                            post("/api/repository")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(creationDto))
                    )
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(RepositoryStatus.NOT_INITIALIZED.name()))
                    .andExpect(jsonPath("$.name").value(creationDto.getName()))
                    .andExpect(jsonPath("$.location").value(creationDto.getLocation()));
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void initialize() throws Exception {
            final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

            asyncMvc
                    .perform(post("/api/repository/{id}/do?action=INITIALIZE", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk());
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void update() throws Exception {
            final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

            final GitRepositoryPatchDto patchDto = GitRepositoryPatchDto.gitBuilder()
                    .id(repository.getId())
                    .defaultBranch("develop")
                    .build();

            asyncMvc
                    .perform(
                            patch("/api/repository/{id}", repository.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(patchDto))
                    )
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.defaultBranch").value("develop"));
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void deleteRepository() throws Exception {
            final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

            asyncMvc
                    .perform(delete("/api/repository/{id}", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isNoContent());

            asyncMvc
                    .perform(get("/api/repository/{id}", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isNotFound());
        }
    }
}
