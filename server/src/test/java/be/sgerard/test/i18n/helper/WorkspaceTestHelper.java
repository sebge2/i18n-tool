package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.repository.dto.RepositoryDto;
import be.sgerard.i18n.model.workspace.WorkspaceDto;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author Sebastien Gerard
 */
@Component
public class WorkspaceTestHelper {

    private final WebTestClient webClient;

    public WorkspaceTestHelper(WebTestClient webClient) {
        this.webClient = webClient;
    }

    <R extends RepositoryDto> StepInitializedRepository<R> with(R repository) {
        return new StepInitializedRepository<>(repository);
    }

    public class StepInitializedRepository<R extends RepositoryDto> {

        private final R repository;

        public StepInitializedRepository(R repository) {
            this.repository = repository;
        }

        @SuppressWarnings("unused")
        public WorkspaceTestHelper and() {
            return WorkspaceTestHelper.this;
        }

        public Collection<WorkspaceDto> get() {
            return webClient.get()
                    .uri("/api/repository/{id}/workspace", repository.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(WorkspaceDto.class)
                    .returnResult()
                    .getResponseBody();
        }

        public RepositoryDto getRepo() {
            return repository;
        }

        public StepInitializedRepository<R> sync() {
            webClient.post()
                    .uri("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", this.repository.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(WorkspaceDto.class)
                    .returnResult()
                    .getResponseBody();

            return new StepInitializedRepository<>(repository);
        }

        public StepNotInitializedWorkspace<R> workspaceForBranch(String branch) {
            return new StepNotInitializedWorkspace<>(
                    repository,
                    findAll()
                            .stream()
                            .filter(workspace -> Objects.equals(workspace.getBranch(), branch))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("There is no workspace with branch [" + branch + "]."))
            );
        }

        private List<WorkspaceDto> findAll() {
            return webClient.get()
                    .uri("/api/repository/{id}/workspace", repository.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(WorkspaceDto.class)
                    .returnResult()
                    .getResponseBody();
        }
    }

    public class StepNotInitializedWorkspace<R extends RepositoryDto> {

        private final R repository;
        private final WorkspaceDto workspace;

        public StepNotInitializedWorkspace(R repository, WorkspaceDto workspace) {
            this.repository = repository;
            this.workspace = workspace;
        }

        @SuppressWarnings("unused")
        public StepInitializedRepository<R> and() {
            return new StepInitializedRepository<>(repository);
        }

        public WorkspaceDto get() {
            return workspace;
        }

        public StepInitializedWorkspace<R> initialize() {
            final WorkspaceDto updatedWorkspace = webClient.post()
                    .uri("/api/repository/workspace/{id}/do?action=INITIALIZE", workspace.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(WorkspaceDto.class)
                    .returnResult()
                    .getResponseBody();

            return new StepInitializedWorkspace<>(repository, updatedWorkspace);
        }
    }

    public class StepInitializedWorkspace<R extends RepositoryDto> {

        private final R repository;
        private final WorkspaceDto workspace;

        public StepInitializedWorkspace(R repository, WorkspaceDto workspace) {
            this.repository = repository;
            this.workspace = workspace;
        }

        @SuppressWarnings("unused")
        public StepInitializedRepository<R> and() {
            return new StepInitializedRepository<>(repository);
        }

        public WorkspaceDto get() {
            return workspace;
        }

        public StepPublishedWorkspace<R> publish(String message) {
            final WorkspaceDto updatedWorkspace = webClient.post()
                    .uri("/api/repository/workspace/{id}/do?action=PUBLISH&message={message}", workspace.getId(), message)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(WorkspaceDto.class)
                    .returnResult()
                    .getResponseBody();

            return new StepPublishedWorkspace<>(repository, updatedWorkspace);
        }
    }

    public class StepPublishedWorkspace<R extends RepositoryDto> {

        private final R repository;
        private final WorkspaceDto workspace;

        public StepPublishedWorkspace(R repository, WorkspaceDto workspace) {
            this.repository = repository;
            this.workspace = workspace;
        }

        @SuppressWarnings("unused")
        public StepInitializedRepository<R> and() {
            return new StepInitializedRepository<>(repository);
        }

        public WorkspaceDto get() {
            return workspace;
        }
    }
}
