package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.*;
import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import be.sgerard.i18n.model.workspace.dto.WorkspaceDto;
import be.sgerard.i18n.model.workspace.dto.WorkspacesPublishRequestDto;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.WithJaneDoeAdminUser;
import org.junit.jupiter.api.*;

import java.util.Locale;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolLocalRepositoryCreationDto;
import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolRepositoryCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.enLocaleCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frLocaleCreationDto;
import static java.util.Arrays.asList;
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
    @CleanupDatabase
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
    @CleanupDatabase
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
    @CleanupDatabase
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
                .jsonPath("$.defaultWorkspace").isEqualTo(true)
                .jsonPath("$.status").isEqualTo(WorkspaceStatus.INITIALIZED.name())
                .jsonPath("$.repositoryId").isEqualTo(repository.forHint("repo").get().getId())
                .jsonPath("$.repositoryType").isEqualTo(RepositoryType.GITHUB.name())
                .jsonPath("$.repositoryName").isEqualTo("sebge2/i18n-tool");
    }

    @Test
    @CleanupDatabase
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
        @CleanupDatabase
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
        @CleanupDatabase
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
        @CleanupDatabase
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
        @CleanupDatabase
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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void synchronizeInReviewReviewFinished() {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            try {
                gitRepo.getRepo(creationDto).createBranches("release/2020.08");

                repository.create(creationDto, GitHubRepositoryDto.class).hint("repo")
                        .initialize()
                        .workspaces()
                        .workspaceForBranch("release/2020.08")
                        .initialize();

                translations.forRepositoryHint("repo")
                        .forWorkspaceName("release/2020.08")
                        .updateTranslation("validation.repository.name-not-unique", Locale.ENGLISH, "my updated value");

                repository.forHint("repo").initialize().workspaces()
                        .workspaceForBranch("release/2020.08").initialize().publish("test");

                repository.forHint("repo").gitHub().findPullRequestForBranchOrDie("release/2020.08").close();

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
                        .jsonPath("$.[3].status").isEqualTo("INITIALIZED");

                translations.forRepositoryHint("repo")
                        .forWorkspaceName("release/2020.08")
                        .translations()
                        .expectNoModification(); // the modification has been published
            } finally {
                gitRepo.getRepo(creationDto).deleteBranches("release/2020.08");
            }
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void synchronizeInReviewReviewFinishedBranchDeleted() {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            try {
                gitRepo.getRepo(creationDto).createBranches("release/2020.08");

                repository.create(creationDto, GitHubRepositoryDto.class).hint("repo")
                        .initialize()
                        .workspaces()
                        .workspaceForBranch("release/2020.08")
                        .initialize()
                        .publish("test");

                repository.forHint("repo").gitHub().findPullRequestForBranchOrDie("release/2020.08").close();
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
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void synchronizeInitializedNewTranslations() {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            try {
                gitRepo.getRepo(creationDto).createBranches("release/2020.08");

                final String workspace = repository.create(creationDto, GitHubRepositoryDto.class).hint("repo")
                        .initialize()
                        .workspaces()
                        .workspaceForBranch("release/2020.08")
                        .initialize().get().getId();

                // once initialized, the remote content is updated, new translation added
                gitRepo.getRepo(creationDto)
                        .branch("release/2020.08")
                        .file("/server/src/main/resources/i18n/validation_en.properties")
                        .writeContent("key-test.synchronizeInitializedNewTranslations=a key written during a test");

                webClient
                        .post()
                        .uri("/api/repository/workspace/{id}/do?action=SYNCHRONIZE", workspace)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody();

                translations
                        .forRepositoryHint("repo")
                        .forWorkspaceName("release/2020.08")
                        .translations()
                        .expectTranslation("key-test.synchronizeInitializedNewTranslations", Locale.ENGLISH, "a key written during a test");
            } finally {
                gitRepo.getRepo(creationDto).deleteBranches("release/2020.08");
            }
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void synchronizeInitializedUpdatedTranslations() {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            try {
                gitRepo.getRepo(creationDto).createBranches("release/2020.08");

                final String workspace = repository.create(creationDto, GitHubRepositoryDto.class).hint("repo")
                        .initialize()
                        .workspaces()
                        .workspaceForBranch("release/2020.08")
                        .initialize().get().getId();

                // once initialized, the remote content is updated, an existing translation is updated
                gitRepo.getRepo(creationDto)
                        .branch("release/2020.08")
                        .file("/server/src/main/resources/i18n/validation_en.properties")
                        .writeContent("validation.repository.name-not-unique=my updated value");

                webClient
                        .post()
                        .uri("/api/repository/workspace/{id}/do?action=SYNCHRONIZE", workspace)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody();

                translations
                        .forRepositoryHint("repo")
                        .forWorkspaceName("release/2020.08")
                        .translations()
                        .expectTranslation("validation.repository.name-not-unique", Locale.ENGLISH, "my updated value");
            } finally {
                gitRepo.getRepo(creationDto).deleteBranches("release/2020.08");
            }
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void synchronizeInitializedLocalUpdatePreserved() {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            try {
                gitRepo.getRepo(creationDto).createBranches("release/2020.08");

                final String workspace = repository.create(creationDto, GitHubRepositoryDto.class).hint("repo")
                        .initialize()
                        .workspaces()
                        .workspaceForBranch("release/2020.08")
                        .initialize().get().getId();

                // once initialized, the remote content is updated, an existing translation is updated
                gitRepo.getRepo(creationDto)
                        .branch("release/2020.08")
                        .file("/server/src/main/resources/i18n/validation_en.properties")
                        .writeContent("validation.repository.name-not-unique=my updated value remote");

                // once initialized, the local content is updated, an existing translation is updated
                translations
                        .forRepositoryHint("repo")
                        .forWorkspaceName("release/2020.08")
                        .updateTranslation("validation.repository.name-not-unique", Locale.ENGLISH, "my updated value local");

                webClient
                        .post()
                        .uri("/api/repository/workspace/{id}/do?action=SYNCHRONIZE", workspace)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody();

                translations
                        .forRepositoryHint("repo")
                        .forWorkspaceName("release/2020.08")
                        .translations()
                        .expectTranslation("validation.repository.name-not-unique", Locale.ENGLISH, "my updated value local");
            } finally {
                gitRepo.getRepo(creationDto).deleteBranches("release/2020.08");
            }
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void synchronizeInitializedDeletedTranslations() {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            try {
                gitRepo.getRepo(creationDto).createBranches("release/2020.08");

                final String workspace = repository.create(creationDto, GitHubRepositoryDto.class).hint("repo")
                        .initialize()
                        .workspaces()
                        .workspaceForBranch("release/2020.08")
                        .initialize().get().getId();

                // once initialized, the remote content is updated, an existing translation is updated
                gitRepo.getRepo(creationDto)
                        .branch("release/2020.08")
                        .file("/server/src/main/resources/i18n/validation_en.properties").writeContent("").and()
                        .file("/server/src/main/resources/i18n/validation_fr.properties").writeContent("");

                webClient
                        .post()
                        .uri("/api/repository/workspace/{id}/do?action=SYNCHRONIZE", workspace)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody();

                translations
                        .forRepositoryHint("repo")
                        .forWorkspaceName("release/2020.08")
                        .translations()
                        .unexpect("validation.repository.name-not-unique");
            } finally {
                gitRepo.getRepo(creationDto).deleteBranches("release/2020.08");
            }
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void synchronizeInitializedDeletedBundleFile() {
            final GitHubRepositoryCreationDto creationDto = i18nToolRepositoryCreationDto();

            try {
                gitRepo.getRepo(creationDto).createBranches("release/2020.08");

                final String workspace = repository.create(creationDto, GitHubRepositoryDto.class).hint("repo")
                        .initialize()
                        .workspaces()
                        .workspaceForBranch("release/2020.08")
                        .initialize().get().getId();

                // once initialized, the remote content is updated, a bundle file is removed
                gitRepo.getRepo(creationDto)
                        .branch("release/2020.08")
                        .file("/server/src/main/resources/i18n/validation_en.properties").remove().and()
                        .file("/server/src/main/resources/i18n/validation_fr.properties").remove();

                webClient
                        .post()
                        .uri("/api/repository/workspace/{id}/do?action=SYNCHRONIZE", workspace)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody();

                translations
                        .forRepositoryHint("repo")
                        .forWorkspaceName("release/2020.08")
                        .translations()
                        .unexpect("validation.repository.name-not-unique");
            } finally {
                gitRepo.getRepo(creationDto).deleteBranches("release/2020.08");
            }
        }

        @Test
        @CleanupDatabase
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
                    .jsonPath("$.lastSynchronization").isNotEmpty()
                    .jsonPath("$.files", hasSize(4));

            translations
                    .forRepositoryHint("my-repo")
                    .forWorkspaceName("master")
                    .translations()
                    .expectTranslation("ResourceNotFoundException.user.message", Locale.ENGLISH, "There is no user with reference [{0}].")
                    .expectTranslation("SHARED.WORKSPACES_TITLE", Locale.ENGLISH, "Workspaces")
                    .expectNoModification();
        }

        @Test
        @CleanupDatabase
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

            repository.forHint("my-repo").gitHub().resetPullRequests();

            webClient
                    .post()
                    .uri("/api/repository/workspace/{id}/do?action=PUBLISH&message=test", masterWorkspace.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.branch").isEqualTo("master")
                    .jsonPath("$.status").isEqualTo(WorkspaceStatus.IN_REVIEW.name());

            final String pullRequestBranch = repository.forHint("my-repo").gitHub()
                    .findPullRequestForBranchOrDie("master")
                    .get().getCurrentBranch();

            gitRepo
                    .getRepo(i18nToolLocalRepositoryCreationDto())
                    .branch(pullRequestBranch)
                    .file("/server/src/main/resources/i18n/validation_en.properties")
//                    .assertContains("validation.repository.name-not-unique=my updated value") TODO fix this
            ;
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void publishAll() {
            repository
                    .create(i18nToolRepositoryCreationDto())
                    .hint("my-repo")
                    .initialize();

            final WorkspaceDto masterWorkspace = repository.forHint("my-repo").initialize().workspaces().workspaceForBranch("master").initialize().get();
            final WorkspaceDto releaseWorkspace = repository.forHint("my-repo").initialize().workspaces().workspaceForBranch("release/2020.05").initialize().get();

            webClient
                    .post()
                    .uri("/api/repository/workspace/do?action=PUBLISH")
                    .bodyValue(
                            WorkspacesPublishRequestDto.builder()
                                    .workspaces(asList(masterWorkspace.getId(), releaseWorkspace.getId()))
                                    .message("publish test")
                                    .build()
                    )
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(2))
                    .jsonPath("$[0].branch").isEqualTo("master")
                    .jsonPath("$[0].status").isEqualTo(WorkspaceStatus.IN_REVIEW.name())
                    .jsonPath("$[1].branch").isEqualTo("release/2020.05")
                    .jsonPath("$[1].status").isEqualTo(WorkspaceStatus.IN_REVIEW.name());
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void publishAllAlreadyPublished() {
            repository
                    .create(i18nToolRepositoryCreationDto())
                    .hint("my-repo")
                    .initialize();

            final WorkspaceDto masterWorkspace = repository.forHint("my-repo").initialize().workspaces().workspaceForBranch("master").initialize().publish("test").get();

            webClient
                    .post()
                    .uri("/api/repository/workspace/do?action=PUBLISH")
                    .bodyValue(
                            WorkspacesPublishRequestDto.builder()
                                    .workspace(masterWorkspace.getId())
                                    .message("publish test")
                                    .build()
                    )
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(1))
                    .jsonPath("$[0].branch").isEqualTo("master")
                    .jsonPath("$[0].status").isEqualTo(WorkspaceStatus.IN_REVIEW.name());
        }

        // TODO workspace NOT INITIALIZED

        @Test
        @CleanupDatabase
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
        @CleanupDatabase
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
        @CleanupDatabase
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
        @CleanupDatabase
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
        @CleanupDatabase
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
                    .jsonPath("$.lastSynchronization").isNotEmpty()
                    .jsonPath("$.files", hasSize(4));

            translations
                    .forRepositoryHint("my-repo")
                    .forWorkspaceName("master")
                    .translations()
                    .expectTranslation("ResourceNotFoundException.user.message", Locale.ENGLISH, "There is no user with reference [{0}].")
                    .expectTranslation("SHARED.WORKSPACES_TITLE", Locale.ENGLISH, "Workspaces")
                    .expectNoModification();
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void publish() {
            final WorkspaceDto masterWorkspace = repository
                    .create(i18nToolLocalRepositoryCreationDto(), GitRepositoryDto.class)
                    .hint("my-repo")
                    .initialize()
                    .workspaces()
                    .workspaceForBranch("master")
                    .initialize()
                    .get();

            translations.forRepositoryHint("my-repo")
                    .forWorkspaceName("master")
                    .updateTranslation("validation.repository.name-not-unique", Locale.ENGLISH, "my updated value");

            webClient
                    .post()
                    .uri("/api/repository/workspace/{id}/do?action=PUBLISH&message=test", masterWorkspace.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.branch").isEqualTo("master")
                    .jsonPath("$.status").isEqualTo(WorkspaceStatus.INITIALIZED.name());

            gitRepo
                    .getRepo(i18nToolLocalRepositoryCreationDto())
                    .branch("master")
                    .file("/server/src/main/resources/i18n/validation_en.properties")
                    .assertContains("validation.repository.name-not-unique=my updated value");
        }

        @Test
        @CleanupDatabase
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
        @CleanupDatabase
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
