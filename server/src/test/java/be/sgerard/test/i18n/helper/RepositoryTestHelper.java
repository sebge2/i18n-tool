package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.RepositoryDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.dto.RepositorySummaryDto;
import be.sgerard.test.i18n.support.JsonHolderResultHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
@Component
public class RepositoryTestHelper {

    private final AsyncMockMvcTestHelper mockMvc;
    private final ObjectMapper objectMapper;
    private final WorkspaceTestHelper workspaceTestHelper;

    public RepositoryTestHelper(AsyncMockMvcTestHelper mockMvc,
                                ObjectMapper objectMapper,
                                WorkspaceTestHelper workspaceTestHelper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.workspaceTestHelper = workspaceTestHelper;
    }

    public StepCreatedRepository<RepositoryDto> create(RepositoryCreationDto creationDto) throws Exception {
        return create(creationDto, RepositoryDto.class);
    }

    public <R extends RepositoryDto> StepCreatedRepository<R> create(RepositoryCreationDto creationDto, Class<R> expectedResult) throws Exception {
        final JsonHolderResultHandler<R> resultHandler = new JsonHolderResultHandler<>(objectMapper, expectedResult);

        mockMvc
                .perform(
                        post("/api/repository")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(creationDto))
                )
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isCreated())
                .andDo(resultHandler)
                .andExpect(jsonPath("$.status").value(RepositoryStatus.NOT_INITIALIZED.name()));

        return new StepCreatedRepository<>(resultHandler.getValue());
    }

    public void clearAll() throws Exception {
        for (RepositorySummaryDto repository : findAll()) {
            delete(repository.getId());
        }
    }

    private Collection<RepositorySummaryDto> findAll() throws Exception {
        final JsonHolderResultHandler<Collection<RepositorySummaryDto>> resultHandler = new JsonHolderResultHandler<>(objectMapper, new TypeReference<>() {
        });

        mockMvc
                .perform(get("/api/repository"))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andDo(resultHandler);

        return resultHandler.getValue();
    }

    private void delete(String repositoryId) throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/repository/{id}", repositoryId))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isNoContent());
    }

    public class StepCreatedRepository<R extends RepositoryDto> {

        private final R repository;

        public StepCreatedRepository(R repository) {
            this.repository = repository;
        }

        @SuppressWarnings("unchecked")
        public StepInitializedRepository<R> initialize() throws Exception {
            final JsonHolderResultHandler<R> resultHandler = new JsonHolderResultHandler<>(objectMapper, (Class<R>) repository.getClass());

            mockMvc
                    .perform(post("/api/repository/{id}/do?action=INITIALIZE", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk())
                    .andDo(resultHandler);

            return new StepInitializedRepository<>(resultHandler.getValue());
        }

        @SuppressWarnings("unchecked")
        public StepCreatedRepository<R> update(RepositoryPatchDto.BaseBuilder<?, ?> patch) throws Exception {
            final JsonHolderResultHandler<R> resultHandler = new JsonHolderResultHandler<>(objectMapper, (Class<R>) repository.getClass());

            mockMvc
                    .perform(
                            patch("/api/repository/{id}", repository.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(patch.id(repository.getId()).build()))
                    )
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk())
                    .andDo(resultHandler);

            return this;
        }

        public R get() {
            return repository;
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

        public RepositoryTestHelper and() {
            return RepositoryTestHelper.this;
        }

        public WorkspaceTestHelper.StepInitializedRepository<R> workspaces() {
            return workspaceTestHelper.with(repository);
        }

        public R get() {
            return repository;
        }
    }
}
