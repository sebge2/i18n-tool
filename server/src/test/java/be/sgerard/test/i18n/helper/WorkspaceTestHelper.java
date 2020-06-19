package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.repository.dto.RepositoryDto;
import be.sgerard.i18n.model.workspace.WorkspaceDto;
import be.sgerard.test.i18n.support.JsonHolderResultHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
@Component
@SuppressWarnings("unused")
public class WorkspaceTestHelper {

    private final AsyncMockMvcTestHelper mockMvc;
    private final ObjectMapper objectMapper;

    public WorkspaceTestHelper(AsyncMockMvcTestHelper mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    <R extends RepositoryDto> StepInitializedRepository<R> with(R repository) {
        return new StepInitializedRepository<>(repository);
    }

    public class StepInitializedRepository<R extends RepositoryDto> {

        private final R repository;

        public StepInitializedRepository(R repository) {
            this.repository = repository;
        }

        public WorkspaceTestHelper and() {
            return WorkspaceTestHelper.this;
        }

        public RepositoryDto get() {
            return repository;
        }

        public StepInitializedRepository<R> sync() throws Exception {
            mockMvc
                    .perform(post("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk());

            return new StepInitializedRepository<>(repository);
        }

        public StepNotInitializedWorkspace<R> workspaceForBranch(String branch) throws Exception {
            return new StepNotInitializedWorkspace<>(
                    repository,
                    findAll()
                            .stream()
                            .filter(workspace -> Objects.equals(workspace.getBranch(), branch))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("There is no workspace with branch [" + branch + "]."))
            );
        }

        private List<WorkspaceDto> findAll() throws Exception {
            final JsonHolderResultHandler<List<WorkspaceDto>> resultHandler = new JsonHolderResultHandler<>(objectMapper, new TypeReference<>() {
            });

            mockMvc
                    .perform(MockMvcRequestBuilders.get("/api/repository/{id}/workspace", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk())
                    .andDo(resultHandler);

            return resultHandler.getValue();
        }
    }

    public class StepNotInitializedWorkspace<R extends RepositoryDto> {

        private final R repository;
        private final WorkspaceDto workspace;

        public StepNotInitializedWorkspace(R repository, WorkspaceDto workspace) {
            this.repository = repository;
            this.workspace = workspace;
        }

        public StepInitializedRepository<R> and() {
            return new StepInitializedRepository<>(repository);
        }

        public WorkspaceDto get() {
            return workspace;
        }

        public StepInitializedWorkspace<R> initialize() throws Exception {
            mockMvc
                    .perform(post("/api/repository/workspace/{id}/do?action=INITIALIZE", workspace.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk());

            return new StepInitializedWorkspace<>(repository, workspace);
        }
    }

    public class StepInitializedWorkspace<R extends RepositoryDto> {

        private final R repository;
        private final WorkspaceDto workspace;

        public StepInitializedWorkspace(R repository, WorkspaceDto workspace) {
            this.repository = repository;
            this.workspace = workspace;
        }

        public StepInitializedRepository<R> and() {
            return new StepInitializedRepository<>(repository);
        }

        public WorkspaceDto get() {
            return workspace;
        }

        public StepPublishedWorkspace<R> publish(String message) throws Exception {
            mockMvc
                    .perform(post("/api/repository/workspace/{id}/do?action=PUBLISH&message={message}", workspace.getId(), message))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk());

            return new StepPublishedWorkspace<>(repository, workspace);
        }
    }

    public class StepPublishedWorkspace<R extends RepositoryDto> {

        private final R repository;
        private final WorkspaceDto workspace;

        public StepPublishedWorkspace(R repository, WorkspaceDto workspace) {
            this.repository = repository;
            this.workspace = workspace;
        }

        public StepInitializedRepository<R> and() {
            return new StepInitializedRepository<>(repository);
        }

        public WorkspaceDto get() {
            return workspace;
        }
    }
}
