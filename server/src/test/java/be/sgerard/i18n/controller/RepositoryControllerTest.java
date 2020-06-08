package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.dto.*;
import be.sgerard.test.i18n.support.GitHubTest;
import be.sgerard.test.i18n.support.WithInternalUser;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolLocalRepositoryCreationDto;
import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolRepositoryCreationDto;
import static be.sgerard.test.i18n.model.UserDtoTestUtils.userJohnDoeCreation;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
public class RepositoryControllerTest extends AbstractControllerTest {

    @BeforeEach
    public void setup() throws Exception {
        userTestHelper.createUser(userJohnDoeCreation().build());
    }

    @AfterEach
    public void clear() throws Exception {
        repositoryTestHelper.clearAll();
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findAll() throws Exception {
        final GitRepositoryDto repository = repositoryTestHelper.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

        asyncMockMvc
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
        final GitRepositoryDto repository = repositoryTestHelper.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

        asyncMockMvc
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
        repositoryTestHelper.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class);

        asyncMockMvc
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
        @GitHubTest
        public void create() throws Exception {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            asyncMockMvc
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
        @GitHubTest
        public void createWrongUrl() throws Exception {
            final GitHubRepositoryCreationDto creationDto = new GitHubRepositoryCreationDto("unknown", "unknown", null);

            asyncMockMvc
                    .perform(
                            post("/api/repository")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(creationDto))
                    )
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isBadRequest());
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        @GitHubTest
        public void createSameName() throws Exception {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            asyncMockMvc
                    .perform(
                            post("/api/repository")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(creationDto))
                    )
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isCreated());

            asyncMockMvc
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
        @GitHubTest
        public void initialize() throws Exception {
            final GitHubRepositoryDto repository = repositoryTestHelper.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            asyncMockMvc
                    .perform(post("/api/repository/{id}/do?action=INITIALIZE", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(RepositoryStatus.INITIALIZED.name()));
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        @GitHubTest
        public void update() throws Exception {
            final GitHubRepositoryDto repository = repositoryTestHelper.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            final GitHubRepositoryPatchDto patchDto = GitHubRepositoryPatchDto.gitHubBuilder()
                    .id(repository.getId())
                    .webHookSecret("a secret")
                    .accessKey("an access key")
                    .build();

            asyncMockMvc
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
        @GitHubTest
        public void deleteRepository() throws Exception {
            final GitHubRepositoryDto repository = repositoryTestHelper.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            asyncMockMvc
                    .perform(delete("/api/repository/{id}", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isNoContent());

            asyncMockMvc
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

            asyncMockMvc
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
            final GitRepositoryDto repository = repositoryTestHelper.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

            asyncMockMvc
                    .perform(post("/api/repository/{id}/do?action=INITIALIZE", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk());
        }

        @Test
        @Transactional
        @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
        public void update() throws Exception {
            final GitRepositoryDto repository = repositoryTestHelper.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

            final GitRepositoryPatchDto patchDto = GitRepositoryPatchDto.gitBuilder()
                    .id(repository.getId())
                    .defaultBranch("develop")
                    .build();

            asyncMockMvc
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
            final GitRepositoryDto repository = repositoryTestHelper.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

            asyncMockMvc
                    .perform(delete("/api/repository/{id}", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isNoContent());

            asyncMockMvc
                    .perform(get("/api/repository/{id}", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isNotFound());
        }
    }
}
