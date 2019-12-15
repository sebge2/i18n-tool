package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.dto.GitHubRepositoryDto;
import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.RepositoryDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sebastien Gerard
 */
@Component
public class RepositoryTestHelper {

    private final WebTestClient webClient;
    private final WorkspaceTestHelper workspaceTestHelper;
    private final GitHubRepositoryMockTestHelper gitHubTestHelper;

    private final Map<String, RepositoryDto> hints = new HashMap<>();

    public RepositoryTestHelper(WebTestClient webClient, WorkspaceTestHelper workspaceTestHelper,
                                GitHubRepositoryMockTestHelper gitHubTestHelper) {
        this.webClient = webClient;
        this.workspaceTestHelper = workspaceTestHelper;
        this.gitHubTestHelper = gitHubTestHelper;
    }

    public StepCreatedRepository<RepositoryDto> create(RepositoryCreationDto creationDto) {
        return create(creationDto, RepositoryDto.class);
    }

    public <R extends RepositoryDto> StepCreatedRepository<R> create(RepositoryCreationDto creationDto, Class<R> expectedResult) {
        final R repository = webClient.post()
                .uri("/api/repository/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(creationDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(expectedResult)
                .returnResult()
                .getResponseBody();

        return new StepCreatedRepository<>(repository);
    }

    public <R extends RepositoryDto> StepCreatedRepository<R> forHint(String hint, Class<R> expectedResult) {
        if (!hints.containsKey(hint)) {
            throw new IllegalArgumentException("No repository created for hint [" + hint + "]");
        }

        return new StepCreatedRepository<>(expectedResult.cast(hints.get(hint)));
    }

    public StepCreatedRepository<RepositoryDto> forHint(String hint) {
        return forHint(hint, RepositoryDto.class);
    }

    public class StepCreatedRepository<R extends RepositoryDto> {

        private final R repository;

        public StepCreatedRepository(R repository) {
            this.repository = repository;
        }

        @SuppressWarnings("unchecked")
        public StepInitializedRepository<R> initialize() {
            final R updatedRepository = webClient.post()
                    .uri("/api/repository/{id}/do?action=INITIALIZE", this.repository.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody((Class<R>) this.repository.getClass())
                    .returnResult()
                    .getResponseBody();

            return new StepInitializedRepository<>(updatedRepository);
        }

        @SuppressWarnings("unchecked")
        public StepCreatedRepository<R> update(RepositoryPatchDto.BaseBuilder<?, ?> patch) {
            final R updatedRepository = webClient.patch()
                    .uri("/api/repository/{id}", this.repository.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(patch.id(repository.getId()).build()))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody((Class<R>) this.repository.getClass())
                    .returnResult()
                    .getResponseBody();

            return new StepCreatedRepository<>(updatedRepository);
        }

        @SuppressWarnings("unused")
        public RepositoryTestHelper and() {
            return RepositoryTestHelper.this;
        }

        public R get() {
            return repository;
        }

        public StepCreatedRepository<R> hint(String hint) {
            hints.put(hint, repository);
            return this;
        }

        public GitHubRepositoryMockTestHelper.StepRepository gitHub() {
            return gitHubTestHelper.forRepository((GitHubRepositoryDto) repository);
        }
    }

    public class StepInitializedRepository<R extends RepositoryDto> {

        private final R repository;

        public StepInitializedRepository(R repository) {
            if (RepositoryStatus.INITIALIZED != repository.getStatus()) {
                throw new IllegalArgumentException("The repository must be initialized, but it is [" + repository.getStatus() + "].");
            }

            this.repository = repository;
        }

        @SuppressWarnings("unused")
        public RepositoryTestHelper and() {
            return RepositoryTestHelper.this;
        }

        public R get() {
            return repository;
        }

        public WorkspaceTestHelper.StepInitializedRepository<R> workspaces() {
            return workspaceTestHelper.with(repository);
        }
    }
}
