package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.github.GitHubPullRequestStatus;
import be.sgerard.i18n.model.repository.dto.*;
import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import be.sgerard.i18n.model.workspace.dto.WorkspaceDto;
import be.sgerard.test.i18n.support.TransactionalReactiveTest;
import be.sgerard.test.i18n.support.WithJaneDoeAdminUser;
import org.junit.jupiter.api.*;

import java.util.Locale;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolLocalRepositoryCreationDto;
import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolRepositoryCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.enLocaleCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frLocaleCreationDto;
import static org.hamcrest.Matchers.*;

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
                .onCurrentGitProject()
                .create()
                .createBranches("release/2020.05", "release/2020.06");

        gitRepo
                .createMockFor(i18nToolLocalRepositoryCreationDto())
                .allowAnonymousRead()
                .onCurrentGitProject()
                .create()
                .createBranches("release/2020.05", "release/2020.06");
    }

    @BeforeEach
    public void setupLocales() {
        locale
                .createLocale(frLocaleCreationDto()).and()
                .createLocale(enLocaleCreationDto());
    }

    @AfterAll
    public void destroy() {
        gitRepo.destroyAll();
    }

    @Test
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void findAll() {
        this.repository
                .create(i18nToolLocalRepositoryCreationDto())
                .initialize()
                .workspaces();

        webClient
                .get()
                .uri("/api/repository/workspace")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").value(hasSize(3));
    }

    @Test
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void findAllOfRepository() {
        final RepositoryDto repository = this.repository
                .create(i18nToolLocalRepositoryCreationDto())
                .initialize()
                .workspaces()
                .getRepo();

        webClient
                .get()
                .uri("/api/repository/{id}/workspace", repository.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").value(hasSize(3));
    }

    @Test
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void findById() {
        final WorkspaceDto masterWorkspace = repository
                .create(i18nToolRepositoryCreationDto())
                .hint("repo")
                .initialize()
                .workspaces()
                .workspaceForBranch("master")
                .get();

        webClient
                .get()
                .uri("/api/repository/workspace/{id}", masterWorkspace.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.branch").isEqualTo("master")
                .jsonPath("$.status").isEqualTo(WorkspaceStatus.INITIALIZED.name())
                .jsonPath("$.repositoryId").isEqualTo(repository.forHint("repo").get().getId());
    }

    @Test
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void findWorkspaceBundleFiles() {
        final WorkspaceDto masterWorkspace = repository
                .create(i18nToolRepositoryCreationDto())
                .hint("repo")
                .initialize()
                .workspaces()
                .workspaceForBranch("master")
                .initialize()
                .get();

        webClient
                .get()
                .uri("/api/repository/workspace/{id}/bundle-file", masterWorkspace.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").value(hasSize(4))
                .jsonPath("$[0].files").value(hasSize(greaterThan(1)))
                .jsonPath("$[1].files").value(hasSize(greaterThan(1)))
                .jsonPath("$[2].files").value(hasSize(greaterThanOrEqualTo(1)))
                .jsonPath("$[3].files").value(hasSize(greaterThan(1)));
    }

    @Nested
    @DisplayName("GitHub")
    class GitHub extends AbstractControllerTest {

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void synchronizeBranchAdded() {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();
            final GitHubRepositoryDto repository = this.repository.create(creationDto, GitHubRepositoryDto.class).initialize().get();

            gitRepo.getRepo(creationDto).createBranches("release/2020.08");

            try {
                webClient
                        .post()
                        .uri("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", repository.getId())
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$").value(hasSize(4))
                        .jsonPath("$.[0].branch").isEqualTo("master")
                        .jsonPath("$.[0].status").isEqualTo("INITIALIZED")
                        .jsonPath("$.[1].branch").isEqualTo("release/2020.05")
                        .jsonPath("$.[1].status").isEqualTo("NOT_INITIALIZED")
                        .jsonPath("$.[2].branch").isEqualTo("release/2020.06")
                        .jsonPath("$.[2].status").isEqualTo("NOT_INITIALIZED")
                        .jsonPath("$.[3].branch").isEqualTo("release/2020.08")
                        .jsonPath("$.[3].status").isEqualTo("NOT_INITIALIZED");
            } finally {
                gitRepo.getRepo(creationDto).deleteBranches("release/2020.08");
            }
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void synchronizeBranchRemoved() {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();
            final GitHubRepositoryDto repository = this.repository.create(creationDto, GitHubRepositoryDto.class).initialize().get();

            try {
                gitRepo.getRepo(creationDto).deleteBranches("release/2020.06");

                webClient
                        .post()
                        .uri("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", repository.getId())
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$").value(hasSize(2))
                        .jsonPath("$.[0].branch").isEqualTo("master")
                        .jsonPath("$.[0].status").isEqualTo("INITIALIZED")
                        .jsonPath("$.[1].branch").isEqualTo("release/2020.05")
                        .jsonPath("$.[1].status").isEqualTo("NOT_INITIALIZED");
            } finally {
                gitRepo.getRepo(creationDto).createBranches("release/2020.06");
            }
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void synchronizeInReviewBranchDeleted() {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            try {
                gitRepo.getRepo(creationDto).createBranches("release/2020.08");

                this.repository.create(creationDto, GitHubRepositoryDto.class).hint("repo")
                        .initialize()
                        .workspaces()
                        .workspaceForBranch("release/2020.08")
                        .initialize()
                        .publish("test");

                gitRepo.getRepo(creationDto).deleteBranches("release/2020.08");

                webClient
                        .post()
                        .uri("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", this.repository.forHint("repo").get().getId())
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$").value(hasSize(3))
                        .jsonPath("$.[0].branch").isEqualTo("master")
                        .jsonPath("$.[0].status").isEqualTo("INITIALIZED")
                        .jsonPath("$.[1].branch").isEqualTo("release/2020.05")
                        .jsonPath("$.[1].status").isEqualTo("NOT_INITIALIZED")
                        .jsonPath("$.[2].branch").isEqualTo("release/2020.06")
                        .jsonPath("$.[2].status").isEqualTo("NOT_INITIALIZED");
            } finally {
                gitRepo.getRepo(creationDto).deleteBranches("release/2020.08");
            }
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void synchronizeInReviewStillInReview() {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            try {
                gitRepo.getRepo(creationDto).createBranches("release/2020.08");

                this.repository.create(creationDto, GitHubRepositoryDto.class).hint("repo")
                        .initialize()
                        .workspaces()
                        .workspaceForBranch("release/2020.08")
                        .initialize()
                        .publish("test");

                webClient
                        .post()
                        .uri("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", this.repository.forHint("repo").get().getId())
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$").value(hasSize(4))
                        .jsonPath("$.[0].branch").isEqualTo("master")
                        .jsonPath("$.[0].status").isEqualTo("INITIALIZED")
                        .jsonPath("$.[1].branch").isEqualTo("release/2020.05")
                        .jsonPath("$.[1].status").isEqualTo("NOT_INITIALIZED")
                        .jsonPath("$.[2].branch").isEqualTo("release/2020.06")
                        .jsonPath("$.[2].status").isEqualTo("NOT_INITIALIZED")
                        .jsonPath("$.[3].branch").isEqualTo("release/2020.08")
                        .jsonPath("$.[3].status").isEqualTo("IN_REVIEW");
            } finally {
                gitRepo.getRepo(creationDto).deleteBranches("release/2020.08");
            }
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void synchronizeInReviewReviewFinished() {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            try {
                gitRepo.getRepo(creationDto).createBranches("release/2020.08");

                this.repository.create(creationDto, GitHubRepositoryDto.class).hint("repo")
                        .initialize()
                        .workspaces()
                        .workspaceForBranch("release/2020.08")
                        .initialize()
                        .publish("test");

                this.repository.forHint("repo").gitHub().updatePullRequestStatus("release/2020.08", GitHubPullRequestStatus.CLOSED);

                webClient
                        .post()
                        .uri("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", this.repository.forHint("repo").get().getId())
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$").value(hasSize(4))
                        .jsonPath("$.[0].branch").isEqualTo("master")
                        .jsonPath("$.[0].status").isEqualTo("INITIALIZED")
                        .jsonPath("$.[1].branch").isEqualTo("release/2020.05")
                        .jsonPath("$.[1].status").isEqualTo("NOT_INITIALIZED")
                        .jsonPath("$.[2].branch").isEqualTo("release/2020.06")
                        .jsonPath("$.[2].status").isEqualTo("NOT_INITIALIZED")
                        .jsonPath("$.[3].branch").isEqualTo("release/2020.08")
                        .jsonPath("$.[3].status").isEqualTo("NOT_INITIALIZED");
            } finally {
                gitRepo.getRepo(creationDto).deleteBranches("release/2020.08");
            }
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void synchronizeInReviewReviewFinishedBranchDeleted() {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            try {
                gitRepo.getRepo(creationDto).createBranches("release/2020.08");

                this.repository.create(creationDto, GitHubRepositoryDto.class).hint("repo")
                        .initialize()
                        .workspaces()
                        .workspaceForBranch("release/2020.08")
                        .initialize()
                        .publish("test");

                gitRepo.getRepo(creationDto).deleteBranches("release/2020.08");
                this.repository.forHint("repo").gitHub().updatePullRequestStatus("release/2020.08", GitHubPullRequestStatus.CLOSED);

                webClient
                        .post()
                        .uri("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", this.repository.forHint("repo").get().getId())
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$").value(hasSize(3))
                        .jsonPath("$.[0].branch").isEqualTo("master")
                        .jsonPath("$.[0].status").isEqualTo("INITIALIZED")
                        .jsonPath("$.[1].branch").isEqualTo("release/2020.05")
                        .jsonPath("$.[1].status").isEqualTo("NOT_INITIALIZED")
                        .jsonPath("$.[2].branch").isEqualTo("release/2020.06")
                        .jsonPath("$.[2].status").isEqualTo("NOT_INITIALIZED");
            } finally {
                gitRepo.getRepo(creationDto).deleteBranches("release/2020.08");
            }
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void initialize() {
            final WorkspaceDto masterWorkspace = repository
                    .create(i18nToolRepositoryCreationDto())
                    .hint("my-repo")
                    .initialize()
                    .workspaces()
                    .workspaceForBranch("master")
                    .get();

            webClient
                    .post()
                    .uri("/api/repository/workspace/{id}/do?action=INITIALIZE", masterWorkspace.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.branch").isEqualTo("master")
                    .jsonPath("$.status").isEqualTo(WorkspaceStatus.INITIALIZED.name())
                    .jsonPath("$.repositoryId").isNotEmpty()
                    .jsonPath("$.files", hasSize(4));

            translations
                    .forRepositoryHint("my-repo")
                    .forWorkspaceName("master")
                    .expectTranslation("ResourceNotFoundException.user.message", Locale.ENGLISH, "There is no user with reference [{0}].")
                    .expectTranslation("SHARED.WORKSPACES_TITLE", Locale.ENGLISH, "Workspaces")
                    .expectNoModification();
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void publish() {
            final WorkspaceDto masterWorkspace = repository
                    .create(i18nToolRepositoryCreationDto())
                    .hint("my-repo")
                    .initialize()
                    .workspaces()
                    .workspaceForBranch("master")
                    .initialize()
                    .get();

            webClient
                    .post()
                    .uri("/api/repository/workspace/{id}/do?action=PUBLISH&message=test", masterWorkspace.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.branch").isEqualTo("master")
                    .jsonPath("$.status").isEqualTo(WorkspaceStatus.IN_REVIEW.name());

            // TODO assert modifications

            repository
                    .forHint("my-repo")
                    .gitHub()
                    .assertHasPullRequests(); // TODO assert PR
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void deleteInitialized() {
            final WorkspaceDto masterWorkspace = repository
                    .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                    .initialize()
                    .workspaces()
                    .workspaceForBranch("master")
                    .initialize()
                    .get();

            webClient
                    .delete()
                    .uri("/api/repository/workspace/{id}", masterWorkspace.getId())
                    .exchange()
                    .expectStatus().isNoContent();
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void deletePublished() {
            final WorkspaceDto masterWorkspace = repository
                    .create(i18nToolRepositoryCreationDto(), GitHubRepositoryDto.class)
                    .initialize()
                    .workspaces()
                    .workspaceForBranch("master")
                    .initialize()
                    .publish("publish message")
                    .get();

            webClient
                    .delete()
                    .uri("/api/repository/workspace/{id}", masterWorkspace.getId())
                    .exchange()
                    .expectStatus().isNoContent();
        }
    }

    @Nested
    @DisplayName("Git")
    class Git extends AbstractControllerTest {

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void synchronizeBranchAdded() {
            final GitRepositoryCreationDto creationDto = i18nToolLocalRepositoryCreationDto();
            final GitRepositoryDto repository = this.repository.create(creationDto, GitRepositoryDto.class).initialize().get();

            gitRepo.getRepo(creationDto).createBranches("release/2020.08");

            try {
                webClient
                        .post()
                        .uri("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", repository.getId())
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$").value(hasSize(4))
                        .jsonPath("$.[0].branch").isEqualTo("master")
                        .jsonPath("$.[0].status").isEqualTo("INITIALIZED")
                        .jsonPath("$.[1].branch").isEqualTo("release/2020.05")
                        .jsonPath("$.[1].status").isEqualTo("NOT_INITIALIZED")
                        .jsonPath("$.[2].branch").isEqualTo("release/2020.06")
                        .jsonPath("$.[2].status").isEqualTo("NOT_INITIALIZED")
                        .jsonPath("$.[3].branch").isEqualTo("release/2020.08")
                        .jsonPath("$.[3].status").isEqualTo("NOT_INITIALIZED");
            } finally {
                gitRepo.getRepo(creationDto).deleteBranches("release/2020.08");
            }
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void synchronizeBranchRemoved() {
            final GitRepositoryCreationDto creationDto = i18nToolLocalRepositoryCreationDto();
            final GitRepositoryDto repository = this.repository.create(creationDto, GitRepositoryDto.class).initialize().get();

            try {
                gitRepo.getRepo(creationDto).deleteBranches("release/2020.06");

                webClient
                        .post()
                        .uri("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", repository.getId())
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$").value(hasSize(2))
                        .jsonPath("$.[0].branch").isEqualTo("master")
                        .jsonPath("$.[0].status").isEqualTo("INITIALIZED")
                        .jsonPath("$.[1].branch").isEqualTo("release/2020.05")
                        .jsonPath("$.[1].status").isEqualTo("NOT_INITIALIZED");
            } finally {
                gitRepo.getRepo(creationDto).createBranches("release/2020.06");
            }
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void initialize() {
            final WorkspaceDto masterWorkspace = repository
                    .create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class)
                    .hint("my-repo")
                    .initialize()
                    .workspaces()
                    .workspaceForBranch("master")
                    .get();

            webClient
                    .post()
                    .uri("/api/repository/workspace/{id}/do?action=INITIALIZE", masterWorkspace.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.branch").isEqualTo("master")
                    .jsonPath("$.status").isEqualTo(WorkspaceStatus.INITIALIZED.name())
                    .jsonPath("$.repositoryId").isNotEmpty()
                    .jsonPath("$.files", hasSize(4));

            translations
                    .forRepositoryHint("my-repo")
                    .forWorkspaceName("master")
                    .expectTranslation("ResourceNotFoundException.user.message", Locale.ENGLISH, "There is no user with reference [{0}].")
                    .expectTranslation("SHARED.WORKSPACES_TITLE", Locale.ENGLISH, "Workspaces")
                    .expectNoModification();
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void publish() {
            final WorkspaceDto masterWorkspace = repository
                    .create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class)
                    .initialize()
                    .workspaces()
                    .workspaceForBranch("master")
                    .initialize()
                    .get();

            webClient
                    .post()
                    .uri("/api/repository/workspace/{id}/do?action=PUBLISH&message=test", masterWorkspace.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.branch").isEqualTo("master")
                    .jsonPath("$.status").isEqualTo(WorkspaceStatus.INITIALIZED.name());
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void deleteInitialized() {
            final WorkspaceDto masterWorkspace = repository
                    .create(i18nToolLocalRepositoryCreationDto())
                    .initialize()
                    .workspaces()
                    .workspaceForBranch("master")
                    .initialize()
                    .get();

            webClient
                    .delete()
                    .uri("/api/repository/workspace/{id}", masterWorkspace.getId())
                    .exchange()
                    .expectStatus().isNoContent();
        }

        @Test
        @TransactionalReactiveTest
        @WithJaneDoeAdminUser
        public void deletePublished() {
            final WorkspaceDto masterWorkspace = repository
                    .create(i18nToolLocalRepositoryCreationDto())
                    .initialize()
                    .workspaces()
                    .workspaceForBranch("master")
                    .initialize()
                    .publish("test message")
                    .get();

            webClient
                    .delete()
                    .uri("/api/repository/workspace/{id}", masterWorkspace.getId())
                    .exchange()
                    .expectStatus().isNoContent();
        }
    }

}
