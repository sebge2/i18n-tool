package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.dto.GitRepositoryDto;
import be.sgerard.i18n.model.snapshot.dto.SnapshotCreationDto;
import be.sgerard.i18n.model.snapshot.dto.SnapshotDto;
import be.sgerard.i18n.support.FileUtils;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.auth.internal.WithJaneDoeAdminUser;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolGitHubRepositoryCreationDto;
import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolGitRepositoryCreationDto;
import static be.sgerard.test.i18n.model.GitRepositoryPatchDtoTestUtils.i18nToolGitHubRepositoryPatchDto;
import static be.sgerard.test.i18n.model.GitRepositoryPatchDtoTestUtils.i18nToolGitRepositoryPatchDto;
import static be.sgerard.test.i18n.model.RepositoryEntityTestUtils.*;
import static org.hamcrest.Matchers.hasSize;

/**
 * @author Sebastien Gerard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SnapshotControllerTest extends AbstractControllerTest {

    @BeforeAll
    public void setupRepo() {
        remoteRepository
                .gitHub()
                .create(i18nToolGitHubRepositoryCreationDto(), "myGitHubRepo")
                .accessToken(I18N_TOOL_GITHUB_ACCESS_TOKEN)
                .onCurrentGitProject()
                .start();

        remoteRepository
                .git()
                .create(i18nToolGitRepositoryCreationDto(), "myGitRepo")
                .addUser(I18N_TOOL_GIT_REPO_USER, I18N_TOOL_GIT_REPO_USER_PASSWORD)
                .onCurrentGitProject()
                .start();

        remoteRepository.gitHub().forHint("myGitHubRepo").manageRemoteBranches().createBranches("develop");
        remoteRepository.git().forHint("myGitRepo").manageRemoteBranches().createBranches("develop");
    }

    @AfterAll
    public void destroy() {
        remoteRepository.stopAll();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void findAll() {
        snapshot.createSnapshot("My first snapshot", null);

        webClient
                .get()
                .uri("/api/snapshot")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").value(hasSize(1))
                .jsonPath("$[0].createdBy").isEqualTo("Jane Doe")
                .jsonPath("$[0].comment").isEqualTo("My first snapshot");
    }

    @Nested
    @DisplayName("Create")
    class Create extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void noPasswordNoComment() {
            webClient
                    .post()
                    .uri("/api/snapshot")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(
                            SnapshotCreationDto.builder()
                                    .build()
                    )
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.createdBy").isEqualTo("Jane Doe")
                    .jsonPath("$.comment").isEmpty();
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void noPassword() {
            webClient
                    .post()
                    .uri("/api/snapshot")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(
                            SnapshotCreationDto
                                    .builder()
                                    .comment("My first snapshot")
                                    .encryptionPassword(null)
                                    .build()
                    )
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.createdBy").isEqualTo("Jane Doe")
                    .jsonPath("$.comment").isEqualTo("My first snapshot");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void withPassword() {
            webClient
                    .post()
                    .uri("/api/snapshot")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(
                            SnapshotCreationDto
                                    .builder()
                                    .comment("My first snapshot")
                                    .encryptionPassword("abzcefz")
                                    .build()
                    )
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.createdBy").isEqualTo("Jane Doe")
                    .jsonPath("$.comment").isEqualTo("My first snapshot");
        }
    }

    @Nested
    @DisplayName("ExportZip")
    class ExportZip extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void noPassword() {
            final SnapshotDto snapshot = this.snapshot.createSnapshot().get();

            webClient
                    .get()
                    .uri("/api/snapshot/{id}/file", snapshot.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType("application/zip")
                    .expectBody()
                    .consumeWith(result -> assertUnzip(result, null));
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void exportPassword() {
            final SnapshotDto snapshot = this.snapshot.createSnapshot("", "my-password").get();

            webClient
                    .get()
                    .uri("/api/snapshot/{id}/file", snapshot.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType("application/zip")
                    .expectBody()
                    .consumeWith(result -> assertUnzip(result, "my-password"));
        }
    }

    @Nested
    @DisplayName("ImportZip")
    class ImportZip extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void noPassword() {
            final File tempDirectory = FileUtils.createTempDirectory("test-export-zip");

            try {
                final File zipFile = new File(tempDirectory, "test.zip");

                this.snapshot
                        .createSnapshot("My first snapshot", null)
                        .downloadTo(zipFile)
                        .delete();

                final MultipartBodyBuilder builder = new MultipartBodyBuilder();
                builder.part("file", new FileSystemResource(zipFile));

                webClient
                        .post()
                        .uri("/api/snapshot/file")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .bodyValue(builder.build())
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody()
                        .jsonPath("$.createdBy").isEqualTo("Jane Doe")
                        .jsonPath("$.comment").isEqualTo("My first snapshot");
            } finally {
                FileUtils.deleteDirectory(tempDirectory);
            }
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void password() {
            final File tempDirectory = FileUtils.createTempDirectory("test-export-zip");

            try {
                final File zipFile = new File(tempDirectory, "test.zip");

                this.snapshot
                        .createSnapshot("My first snapshot", "my-password")
                        .downloadTo(zipFile)
                        .delete();

                final MultipartBodyBuilder builder = new MultipartBodyBuilder();
                builder.part("file", new FileSystemResource(zipFile));
                builder.part("encryptionPassword", "my-password");

                webClient
                        .post()
                        .uri("/api/snapshot/file")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .bodyValue(builder.build())
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBody()
                        .jsonPath("$.createdBy").isEqualTo("Jane Doe")
                        .jsonPath("$.comment").isEqualTo("My first snapshot");
            } finally {
                FileUtils.deleteDirectory(tempDirectory);
            }
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void wrongPassword() {
            final File tempDirectory = FileUtils.createTempDirectory("test-export-zip");

            try {
                final File zipFile = new File(tempDirectory, "test.zip");

                this.snapshot
                        .createSnapshot("My first snapshot", "my-password")
                        .downloadTo(zipFile)
                        .delete();

                final MultipartBodyBuilder builder = new MultipartBodyBuilder();
                builder.part("file", new FileSystemResource(zipFile));
                builder.part("encryptionPassword", "anotherPassword");

                webClient
                        .post()
                        .uri("/api/snapshot/file")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .bodyValue(builder.build())
                        .exchange()
                        .expectStatus().isBadRequest();
            } finally {
                FileUtils.deleteDirectory(tempDirectory);
            }
        }
    }

    @Nested
    @DisplayName("Restore")
    class Restore extends AbstractControllerTest {

        @TempDir
        public File tempDir;

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void restore() {
            this.repository.create(i18nToolGitRepositoryCreationDto(), GitRepositoryDto.class)
                    .hint("git")
                    .update(i18nToolGitRepositoryPatchDto())
                    .initialize()
                    .workspaces()
                    .workspaceForBranch("master").initialize();
            this.repository.create(i18nToolGitHubRepositoryCreationDto(), GitRepositoryDto.class)
                    .hint("gitHub")
                    .update(i18nToolGitHubRepositoryPatchDto())
                    .initialize()
                    .workspaces()
                    .workspaceForBranch("master").initialize();

            this.user.initAdminUser();

            final File file = new File(tempDir, "export.zip");

            final SnapshotDto snapshot = this.snapshot.createSnapshot().downloadTo(file).get();

            webClient
                    .post()
                    .uri("/api/snapshot/{id}/do?action=RESTORE", snapshot.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody();

            this.repository.forHint("git").initialize().workspaces().sync();
            this.repository.forHint("gitHub").initialize().workspaces().sync();
        }
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void delete() {
        final SnapshotDto snapshot = this.snapshot.createSnapshot().get();

        webClient
                .delete()
                .uri("/api/snapshot/{id}", snapshot.getId())
                .exchange()
                .expectStatus().isNoContent();

        webClient
                .get()
                .uri("/api/snapshot/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").value(hasSize(0));
    }

    private void assertUnzip(EntityExchangeResult<byte[]> result, String encryptionPassword) {
        final File tempDirectory = FileUtils.createTempDirectory("test-export-zip");
        final File zipFile = new File(tempDirectory, "test.zip");

        try (OutputStream outputStream = new FileOutputStream(zipFile)) {
            IOUtils.write(result.getResponseBody(), outputStream);

            FileUtils.unzipDirectory(tempDirectory, zipFile, encryptionPassword);

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
