package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.dto.*;
import be.sgerard.test.i18n.support.WithInternalUser;
import org.junit.Before;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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

    @Before
    public void setup() throws Exception {
        userTestHelper.createUser(userJohnDoeCreation().build());
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findAll() throws Exception {
        final GitHubRepositoryDto repository = repositoryTestHelper.createRepository(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class);

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
        final GitHubRepositoryDto repository = repositoryTestHelper.createRepository(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class);

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
        repositoryTestHelper.createRepository(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class);

        asyncMockMvc
                .perform(get("/api/repository/{id}", "unknown"))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void createGitHubRepository() throws Exception {
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
    public void createGitRepository() throws Exception {
        final GitRepositoryCreationDto creationDto = i18nToolLocalRepositoryCreationDto();

        final GitRepositoryDto repository = repositoryTestHelper.createRepository(creationDto, GitRepositoryDto.class);
//        asyncMockMvc
//                .perform(
//                        post("/api/repository")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(creationDto))
//                )
//                .andExpectStarted()
//                .andWaitResult()
//                .andExpect(status().isCreated())
//        .andDo(print())
////                .andExpect(jsonPath("$.status").value(RepositoryStatus.NOT_INITIALIZED.name()))
////                .andExpect(jsonPath("$.name").value("sebge2/i18n-tool"))
////                .andExpect(jsonPath("$.location").value("https://github.com/sebge2/i18n-tool.git"))
//        ;

        final GitRepositoryDto gitRepositoryDto = repositoryTestHelper.initializeRepository(repository.getId(), GitRepositoryDto.class);

        System.out.println(gitRepositoryDto);
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void createGitHubRepositoryWrongUrl() throws Exception {
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
    public void createGitHubRepositorySameName() throws Exception {
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
    @Tag("GitHub")
    public void initializeGitHubRepository() throws Exception {
        if(true){
            throw new RuntimeException("bam");
        }

        final GitHubRepositoryDto repository = repositoryTestHelper.createRepository(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class);

        asyncMockMvc
                .perform(post("/api/repository/{id}/do?action=INITIALIZE", repository.getId()))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void updateGitHubRepository() throws Exception {
        final GitHubRepositoryDto repository = repositoryTestHelper.createRepository(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class);

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
    public void deleteGitHubRepository() throws Exception {
        final GitHubRepositoryDto repository = repositoryTestHelper.createRepository(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class);

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
