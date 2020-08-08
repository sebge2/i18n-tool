package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.dto.*;
import be.sgerard.test.i18n.support.TransactionalReactiveTest;
import be.sgerard.test.i18n.support.WithJaneDoeAdminUser;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

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
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void findAll() {
        final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

        webClient.get()
                .uri("/api/repository/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").value(hasSize(greaterThanOrEqualTo(1)))
                .jsonPath("$[?(@.id=='" + repository.getId() + "')]").exists()
                .jsonPath("$[?(@.name=='" + repository.getName() + "')]").exists()
                .jsonPath("$[?(@.status=='" + RepositoryStatus.NOT_INITIALIZED.name() + "')]").exists();
    }

    @Test
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void findById() {
        final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

        webClient.get()
                .uri("/api/repository/{id}", repository.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(RepositoryStatus.NOT_INITIALIZED.name())
                .jsonPath("$.name").isEqualTo(repository.getName())
                .jsonPath("$.location").isEqualTo(repository.getLocation());
    }

    @Test
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void findByIdNotFound() {
        repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class);

        webClient.get()
                .uri("/api/repository/{id}", "unknown")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Nested
    @DisplayName("GitHub")
    class GitHub extends AbstractControllerTest {

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void create() {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            webClient.post()
                    .uri("/api/repository/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(creationDto)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(RepositoryStatus.NOT_INITIALIZED.name())
                    .jsonPath("$.name").isEqualTo("sebge2/i18n-tool")
                    .jsonPath("$.location").isEqualTo("https://github.com/sebge2/i18n-tool.git")
                    .jsonPath("$.defaultBranch").isEqualTo("master")
                    .jsonPath("$.allowedBranches").isEqualTo("^master|develop|release\\/[0-9]{4}.[0-9]{1,2}$")
                    .jsonPath("$.username").isEqualTo("sebge2")
                    .jsonPath("$.repository").isEqualTo("i18n-tool");
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void createWrongUrl() {
            final GitHubRepositoryCreationDto creationDto = new GitHubRepositoryCreationDto("unknown", "unknown", null);

            webClient.post()
                    .uri("/api/repository/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(creationDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("The Git repository [https://github.com/unknown/unknown.git] has not been found. Please verify the URL.");
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void createInvalidAccessKeyCredentials() {
            final GitHubRepositoryCreationDto creationDto = new GitHubRepositoryCreationDto("sebge2", "private-i18n-tool", "ZEF");

            webClient.post()
                    .uri("/api/repository/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(creationDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("Please verify your credentials for accessing the Git repository [https://github.com/sebge2/private-i18n-tool.git].");
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void createNoAccessKeyCredentials() {
            final GitHubRepositoryCreationDto creationDto = new GitHubRepositoryCreationDto("sebge2", "private-i18n-tool", null);

            webClient.post()
                    .uri("/api/repository/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(creationDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("Please verify your credentials for accessing the Git repository [https://github.com/sebge2/private-i18n-tool.git].");
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void createValidAccessKeyCredentials() {
            final GitHubRepositoryCreationDto creationDto = privateI18nToolRepositoryCreationDto();

            webClient.post()
                    .uri("/api/repository/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(creationDto)
                    .exchange()
                    .expectStatus().isCreated();
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void createSameName() {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            webClient.post()
                    .uri("/api/repository/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(creationDto)
                    .exchange()
                    .expectStatus().isCreated();

            webClient.post()
                    .uri("/api/repository/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(creationDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("Another repository is already named [sebge2/i18n-tool]. Names must be unique.");
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void initialize() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            webClient.post()
                    .uri("/api/repository/{id}/do?action=INITIALIZE", repository.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(RepositoryStatus.INITIALIZED.name());
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void initializeTwice() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            webClient.post()
                    .uri("/api/repository/{id}/do?action=INITIALIZE", repository.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(RepositoryStatus.INITIALIZED.name());

            webClient.post()
                    .uri("/api/repository/{id}/do?action=INITIALIZE", repository.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(RepositoryStatus.INITIALIZED.name());
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void updateWebHookAndAccessKey() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            final GitHubRepositoryPatchDto patchDto = GitHubRepositoryPatchDto.gitHubBuilder()
                    .id(repository.getId())
                    .webHookSecret("a secret")
                    .accessKey("an access key")
                    .build();

            webClient
                    .patch()
                    .uri("/api/repository/{id}", repository.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void updateAllowedBranch() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            final GitHubRepositoryPatchDto patchDto = GitHubRepositoryPatchDto.gitHubBuilder()
                    .id(repository.getId())
                    .allowedBranches("^master$")
                    .build();

            webClient
                    .patch()
                    .uri("/api/repository/{id}", repository.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void updateAllowedBranchDefaultBranchNotMatching() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            final GitHubRepositoryPatchDto patchDto = GitHubRepositoryPatchDto.gitHubBuilder()
                    .id(repository.getId())
                    .allowedBranches("^release\\/[0-9]{4}.[0-9]{1,2}$")
                    .build();

            webClient
                    .patch()
                    .uri("/api/repository/{id}", repository.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("The default branch [master] is not allowed by the pattern [^release\\/[0-9]{4}.[0-9]{1,2}$].");
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void updateAllowedBranchPatternInvalid() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            final GitHubRepositoryPatchDto patchDto = GitHubRepositoryPatchDto.gitHubBuilder()
                    .id(repository.getId())
                    .allowedBranches("[")
                    .build();

            webClient
                    .patch()
                    .uri("/api/repository/{id}", repository.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("The pattern specifying allowed branches is invalid: [[].");
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void updateDefaultBranch() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            final GitHubRepositoryPatchDto patchDto = GitHubRepositoryPatchDto.gitHubBuilder()
                    .id(repository.getId())
                    .defaultBranch("develop")
                    .build();

            webClient
                    .patch()
                    .uri("/api/repository/{id}", repository.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void updateDefaultBranchNotMatching() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            final GitHubRepositoryPatchDto patchDto = GitHubRepositoryPatchDto.gitHubBuilder()
                    .id(repository.getId())
                    .defaultBranch("toto")
                    .build();

            webClient
                    .patch()
                    .uri("/api/repository/{id}", repository.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("The default branch [toto] is not allowed by the pattern [^master|develop|release\\/[0-9]{4}.[0-9]{1,2}$].");
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void deleteRepository() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            webClient.delete()
                    .uri("/api/repository/{id}", repository.getId())
                    .exchange()
                    .expectStatus().isNoContent();

            webClient.get()
                    .uri("/api/repository/{id}", repository.getId())
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    @DisplayName("Git")
    class Git extends AbstractControllerTest {

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void create() {
            final GitRepositoryCreationDto creationDto = i18nToolLocalRepositoryCreationDto();

            webClient.post()
                    .uri("/api/repository/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(creationDto)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(RepositoryStatus.NOT_INITIALIZED.name())
                    .jsonPath("$.name").isEqualTo(creationDto.getName())
                    .jsonPath("$.location").isEqualTo(creationDto.getLocation())
                    .jsonPath("$.defaultBranch").isEqualTo("master")
                    .jsonPath("$.allowedBranches").isEqualTo("^master|develop|release\\/[0-9]{4}.[0-9]{1,2}$");
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void initialize() {
            final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

            webClient.post()
                    .uri("/api/repository/{id}/do?action=INITIALIZE", repository.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(RepositoryStatus.INITIALIZED.name());
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void updateAllowedBranch() {
            final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

            final GitRepositoryPatchDto patchDto = GitRepositoryPatchDto.gitBuilder()
                    .id(repository.getId())
                    .allowedBranches("^master$")
                    .build();

            webClient
                    .patch()
                    .uri("/api/repository/{id}", repository.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void updateAllowedBranchDefaultBranchNotMatching() {
            final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

            final GitRepositoryPatchDto patchDto = GitRepositoryPatchDto.gitBuilder()
                    .id(repository.getId())
                    .allowedBranches("^release\\/[0-9]{4}.[0-9]{1,2}$")
                    .build();

            webClient
                    .patch()
                    .uri("/api/repository/{id}", repository.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("The default branch [master] is not allowed by the pattern [^release\\/[0-9]{4}.[0-9]{1,2}$].");
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void updateAllowedBranchPatternInvalid() {
            final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

            final GitRepositoryPatchDto patchDto = GitRepositoryPatchDto.gitBuilder()
                    .id(repository.getId())
                    .allowedBranches("[")
                    .build();

            webClient
                    .patch()
                    .uri("/api/repository/{id}", repository.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("The pattern specifying allowed branches is invalid: [[].");
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void updateDefaultBranch() {
            final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

            final GitRepositoryPatchDto patchDto = GitRepositoryPatchDto.gitBuilder()
                    .id(repository.getId())
                    .defaultBranch("develop")
                    .build();

            webClient
                    .patch()
                    .uri("/api/repository/{id}", repository.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void updateDefaultBranchNotMatching() {
            final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

            final GitRepositoryPatchDto patchDto = GitRepositoryPatchDto.gitBuilder()
                    .id(repository.getId())
                    .defaultBranch("toto")
                    .build();

            webClient
                    .patch()
                    .uri("/api/repository/{id}", repository.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("The default branch [toto] is not allowed by the pattern [^master|develop|release\\/[0-9]{4}.[0-9]{1,2}$].");
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void deleteRepository() {
            final GitRepositoryDto repository = this.repository.create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class).get();

            webClient.delete()
                    .uri("/api/repository/{id}", repository.getId())
                    .exchange()
                    .expectStatus().isNoContent();

            webClient.get()
                    .uri("/api/repository/{id}", repository.getId())
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }
}
