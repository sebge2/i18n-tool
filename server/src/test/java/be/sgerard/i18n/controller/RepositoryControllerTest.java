package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.dto.*;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.auth.internal.WithJaneDoeAdminUser;
import be.sgerard.test.i18n.support.auth.external.github.WithGarrickKleinAdminUser;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.*;
import static be.sgerard.test.i18n.model.RepositoryEntityTestUtils.*;
import static be.sgerard.test.i18n.model.UserEntityTestUtils.GARRICK_KLEIN_TOKEN;
import static org.hamcrest.Matchers.*;

/**
 * @author Sebastien Gerard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RepositoryControllerTest extends AbstractControllerTest {

    @BeforeAll
    public void setupRepo() {
        remoteRepository
                .gitHub()
                .create(i18nToolGitHubRepositoryCreationDto(), "myGitHubRepo")
                .accessToken(I18N_TOOL_GITHUB_ACCESS_TOKEN)
                .accessToken("another-valid-access-token")
                .accessToken(GARRICK_KLEIN_TOKEN)
                .onCurrentGitProject()
                .start();

        remoteRepository
                .git()
                .create(i18nToolGitRepositoryCreationDto(), "myGitRepo")
                .addUser(I18N_TOOL_GIT_REPO_USER, I18N_TOOL_GIT_REPO_USER_PASSWORD)
                .addUser("another-valid-user", "another-password")
                .onCurrentGitProject()
                .start();
    }

    @AfterAll
    public void destroy() {
        remoteRepository.stopAll();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void findAll() {
        final GitRepositoryDto repository = this.repository.create(i18nToolGitRepositoryCreationDto(), GitRepositoryDto.class).get();

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
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void findById() {
        final GitRepositoryDto repository = this.repository.create(i18nToolGitRepositoryCreationDto(), GitRepositoryDto.class).get();

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
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void findByIdNotFound() {
        repository.create(i18nToolGitRepositoryCreationDto(), GitRepositoryDto.class);

        webClient.get()
                .uri("/api/repository/{id}", "unknown")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Nested
    @DisplayName("GitHub")
    class GitHub extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void create() {
            final GitHubRepositoryCreationDto creationDto = i18nToolGitHubRepositoryCreationDto();

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
        @CleanupDatabase
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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void createInvalidAccessKeyCredentials() {
            final GitHubRepositoryCreationDto creationDto =
                    new GitHubRepositoryCreationDto(i18nToolGitHubRepositoryCreationDto().getUsername(), i18nToolGitHubRepositoryCreationDto().getRepository(), "ZEF");

            webClient.post()
                    .uri("/api/repository/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(creationDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("Please verify your credentials for accessing the Git repository [https://github.com/sebge2/i18n-tool.git].");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void createNoAccessKeyCredentials() {
            final GitHubRepositoryCreationDto creationDto = i18nToolGitHubRepositoryCreationNoAccessTokenDto();

            webClient.post()
                    .uri("/api/repository/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(creationDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("Please verify your credentials for accessing the Git repository [https://github.com/sebge2/i18n-tool.git].");
        }

        @Test
        @CleanupDatabase
        @WithGarrickKleinAdminUser
        public void createValidAccessCurrentUserHasCredentials() {
            final GitHubRepositoryCreationDto creationDto = i18nToolGitHubRepositoryCreationNoAccessTokenDto();

            webClient.post()
                    .uri("/api/repository/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(creationDto)
                    .exchange()
                    .expectStatus().isCreated();
        }

        @Test
        @CleanupDatabase
        @WithGarrickKleinAdminUser
        public void createValidAccessCurrentUserHasCredentialsButInvalidToken() {
            final GitHubRepositoryCreationDto creationDto =
                    new GitHubRepositoryCreationDto(i18nToolGitHubRepositoryCreationDto().getUsername(), i18nToolGitHubRepositoryCreationDto().getRepository(), "ZEF");

            webClient.post()
                    .uri("/api/repository/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(creationDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("Please verify your credentials for accessing the Git repository [https://github.com/sebge2/i18n-tool.git].");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void createSameName() {
            final GitHubRepositoryCreationDto creationDto = i18nToolGitHubRepositoryCreationDto();

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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void initialize() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolGitHubRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            webClient.post()
                    .uri("/api/repository/{id}/do?action=INITIALIZE", repository.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(RepositoryStatus.INITIALIZED.name());
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void initializeTwice() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolGitHubRepositoryCreationDto(), GitHubRepositoryDto.class).get();

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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateWebHookAndAccessKey() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolGitHubRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            final GitHubRepositoryPatchDto patchDto = GitHubRepositoryPatchDto.gitHubBuilder()
                    .id(repository.getId())
                    .webHookSecret("a secret")
                    .accessKey("another-valid-access-token")
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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateAllowedBranch() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolGitHubRepositoryCreationDto(), GitHubRepositoryDto.class).get();

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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateAllowedBranchDefaultBranchNotMatching() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolGitHubRepositoryCreationDto(), GitHubRepositoryDto.class).get();

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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateAllowedBranchPatternInvalid() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolGitHubRepositoryCreationDto(), GitHubRepositoryDto.class).get();

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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateDefaultBranch() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolGitHubRepositoryCreationDto(), GitHubRepositoryDto.class).get();

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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateDefaultBranchNotMatching() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolGitHubRepositoryCreationDto(), GitHubRepositoryDto.class).get();

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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateAccessKey() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolGitHubRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            final GitHubRepositoryPatchDto patchDto = GitHubRepositoryPatchDto.gitHubBuilder()
                    .id(repository.getId())
                    .accessKey("another-valid-access-token")
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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateAccessKeyInvalid() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolGitHubRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            final GitHubRepositoryPatchDto patchDto = GitHubRepositoryPatchDto.gitHubBuilder()
                    .id(repository.getId())
                    .accessKey("abzec")
                    .build();

            webClient
                    .patch()
                    .uri("/api/repository/{id}", repository.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]")
                    .isEqualTo("Please verify your credentials for accessing the Git repository [https://github.com/sebge2/i18n-tool.git].");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateWebHookSecret() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolGitHubRepositoryCreationDto(), GitHubRepositoryDto.class).get();

            final GitHubRepositoryPatchDto patchDto = GitHubRepositoryPatchDto.gitHubBuilder()
                    .id(repository.getId())
                    .webHookSecret("abzec")
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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void deleteRepository() {
            final GitHubRepositoryDto repository = this.repository.create(i18nToolGitHubRepositoryCreationDto(), GitHubRepositoryDto.class).get();

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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void create() {
            final GitRepositoryCreationDto creationDto = i18nToolGitRepositoryCreationDto();

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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void createInvalidUserCredentials() {
            final GitRepositoryCreationDto creationDto =
                    new GitRepositoryCreationDto(i18nToolGitRepositoryCreationNoUserDto().getLocation(),
                            i18nToolGitRepositoryCreationNoUserDto().getName(), "a", "b");

            webClient.post()
                    .uri("/api/repository/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(creationDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").value(containsString("Please verify your credentials for accessing the Git repository ["));
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void createNoUserCredentials() {
            final GitRepositoryCreationDto creationDto = i18nToolGitRepositoryCreationNoUserDto();

            webClient.post()
                    .uri("/api/repository/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(creationDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").value(containsString("Please verify your credentials for accessing the Git repository ["));
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void createSameName() {
            final GitRepositoryCreationDto creationDto = i18nToolGitRepositoryCreationDto();

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
                    .jsonPath("$.messages[0]").isEqualTo("Another repository is already named [i18n-tool]. Names must be unique.");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void initialize() {
            final GitRepositoryDto repository = this.repository.create(i18nToolGitRepositoryCreationDto(), GitRepositoryDto.class).get();

            webClient.post()
                    .uri("/api/repository/{id}/do?action=INITIALIZE", repository.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(RepositoryStatus.INITIALIZED.name());
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateAllowedBranch() {
            final GitRepositoryDto repository = this.repository.create(i18nToolGitRepositoryCreationDto(), GitRepositoryDto.class).get();

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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateName() {
            final GitRepositoryDto repository = this.repository.create(i18nToolGitRepositoryCreationDto(), GitRepositoryDto.class).get();

            final GitRepositoryPatchDto patchDto = GitRepositoryPatchDto.gitBuilder()
                    .id(repository.getId())
                    .name("My beautiful repository")
                    .build();

            webClient
                    .patch()
                    .uri("/api/repository/{id}", repository.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.name").isEqualTo("My beautiful repository");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateAllowedBranchDefaultBranchNotMatching() {
            final GitRepositoryDto repository = this.repository.create(i18nToolGitRepositoryCreationDto(), GitRepositoryDto.class).get();

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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateAllowedBranchPatternInvalid() {
            final GitRepositoryDto repository = this.repository.create(i18nToolGitRepositoryCreationDto(), GitRepositoryDto.class).get();

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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateDefaultBranch() {
            final GitRepositoryDto repository = this.repository.create(i18nToolGitRepositoryCreationDto(), GitRepositoryDto.class).get();

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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateDefaultBranchNotMatching() {
            final GitRepositoryDto repository = this.repository.create(i18nToolGitRepositoryCreationDto(), GitRepositoryDto.class).get();

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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateUserPassword() {
            final GitRepositoryDto repository = this.repository.create(i18nToolGitRepositoryCreationDto(), GitRepositoryDto.class).get();

            final GitRepositoryPatchDto patchDto = GitRepositoryPatchDto.gitBuilder()
                    .id(repository.getId())
                    .username("another-valid-user")
                    .password("another-password")
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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void updateUserPasswordInvalid() {
            final GitRepositoryDto repository = this.repository.create(i18nToolGitRepositoryCreationDto(), GitRepositoryDto.class).get();

            final GitRepositoryPatchDto patchDto = GitRepositoryPatchDto.gitBuilder()
                    .id(repository.getId())
                    .username("an-invalid-user")
                    .password("another-password")
                    .build();

            webClient
                    .patch()
                    .uri("/api/repository/{id}", repository.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patchDto)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").value(containsString("Please verify your credentials for accessing the Git repository ["));
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void deleteRepository() {
            final GitRepositoryDto repository = this.repository.create(i18nToolGitRepositoryCreationDto(), GitRepositoryDto.class).get();

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
