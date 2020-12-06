package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.snapshot.dto.SnapshotCreationDto;
import be.sgerard.i18n.model.snapshot.dto.SnapshotDto;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author Sebastien Gerard
 */
@Component
public class SnapshotTestHelper {

    private final WebTestClient webClient;

    public SnapshotTestHelper(WebTestClient webClient) {
        this.webClient = webClient;
    }

    public StepSnapshot createSnapshot() {
        return createSnapshot(null, null);
    }

    public StepSnapshot createSnapshot(String comment, String encryptionPassword) {
        final SnapshotDto snapshot = webClient
                .post()
                .uri("/api/snapshot")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        SnapshotCreationDto
                                .builder()
                                .comment(comment)
                                .encryptionPassword(encryptionPassword)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(SnapshotDto.class)
                .returnResult()
                .getResponseBody();

        return new StepSnapshot(snapshot);
    }

    public class StepSnapshot {

        private final SnapshotDto snapshot;

        public StepSnapshot(SnapshotDto snapshot) {
            this.snapshot = snapshot;
        }

        public SnapshotDto get() {
            return snapshot;
        }

        public StepSnapshot downloadTo(File file) {
            webClient
                    .get()
                    .uri("/api/snapshot/{id}/file", snapshot.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType("application/zip")
                    .expectBody()
                    .consumeWith(result -> {
                        try (OutputStream outputStream = new FileOutputStream(file)) {
                            file.getParentFile().mkdirs();

                            IOUtils.write(result.getResponseBody(), outputStream);

                        } catch (Exception e) {
                            Assert.fail(e.getMessage());
                        }
                    });

            return this;
        }

        public SnapshotTestHelper delete() {
            webClient
                    .delete()
                    .uri("/api/snapshot/{id}", snapshot.getId())
                    .exchange()
                    .expectStatus().isNoContent();

            return SnapshotTestHelper.this;
        }
    }
}
