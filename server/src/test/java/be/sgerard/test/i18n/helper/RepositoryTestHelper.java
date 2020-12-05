package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.RepositoryDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
@Component
public class RepositoryTestHelper {

    private final WebTestClient webClient;
    private final WorkspaceTestHelper workspaceTestHelper;

    private final Map<String, String> hints = new HashMap<>();

    public RepositoryTestHelper(WebTestClient webClient, WorkspaceTestHelper workspaceTestHelper) {
        this.webClient = webClient;
        this.workspaceTestHelper = workspaceTestHelper;
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

        final R repository = webClient.get()
                .uri("/api/repository/{id}", hints.get(hint))
                .exchange()
                .expectStatus().isOk()
                .expectBody(expectedResult)
                .returnResult()
                .getResponseBody();

        return new StepCreatedRepository<>(repository);
    }

    public StepCreatedRepository<RepositoryDto> forHint(String hint) {
        return forHint(hint, RepositoryDto.class);
    }

    public class StepCreatedRepository<R extends RepositoryDto> {

        protected final R repository;

        public StepCreatedRepository(R repository) {
            this.repository = repository;
        }

        @SuppressWarnings("unused")
        public RepositoryTestHelper and() {
            return RepositoryTestHelper.this;
        }

        public R get() {
            return repository;
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

            assertThat(updatedRepository).isNotNull();

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

        @SuppressWarnings("UnusedReturnValue")
        public RepositoryTestHelper delete() {
            webClient.delete()
                    .uri("/api/repository/{id}", this.repository.getId())
                    .exchange()
                    .expectStatus().isNoContent();

            return RepositoryTestHelper.this;
        }

        public StepCreatedRepository<R> hint(String hint) {
            hints.put(hint, repository.getId());
            return this;
        }

        public <B extends RepositoryDto> StepCreatedRepository<B> cast(Class<B> dtoType) {
            return new StepCreatedRepository<>(dtoType.cast(repository));
        }
    }

    public class StepInitializedRepository<R extends RepositoryDto> extends StepCreatedRepository<R> {

        public StepInitializedRepository(R repository) {
            super(repository);

            if (RepositoryStatus.INITIALIZED != repository.getStatus()) {
                throw new IllegalArgumentException("The repository must be initialized, but it is [" + repository.getStatus() + "].");
            }
        }

        public WorkspaceTestHelper.StepInitializedRepository<R> workspaces() {
            return workspaceTestHelper.with(repository);
        }

        public <B extends RepositoryDto> StepInitializedRepository<B> cast(Class<B> dtoType) {
            return new StepInitializedRepository<>(dtoType.cast(repository));
        }
    }
}
