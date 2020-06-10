package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.repository.dto.RepositoryDto;
import be.sgerard.i18n.model.workspace.WorkspaceDto;
import be.sgerard.test.i18n.support.JsonHolderResultHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
@Component
public class WorkspaceTestHelper {

    private final AsyncMockMvcTestHelper mockMvc;
    private final ObjectMapper objectMapper;

    public WorkspaceTestHelper(AsyncMockMvcTestHelper mockMvc,
                               ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public StepInitializedRepository with(RepositoryDto repository) {
        return new StepInitializedRepository(repository);
    }

    public class StepInitializedRepository {

        private final RepositoryDto repository;

        public StepInitializedRepository(RepositoryDto repository) {
            this.repository = repository;
        }

        public StepSynchronizedWorkspaces sync() throws Exception {
            final JsonHolderResultHandler<List<WorkspaceDto>> resultHandler = new JsonHolderResultHandler<>(objectMapper, new TypeReference<>() {
            });

            mockMvc
                    .perform(post("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", repository.getId()))
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().isOk())
                    .andDo(resultHandler);

            return new StepSynchronizedWorkspaces(repository, resultHandler.getValue());
        }
    }

    public class StepSynchronizedWorkspaces {

        private final RepositoryDto repository;
        private final List<WorkspaceDto> workspaces;

        public StepSynchronizedWorkspaces(RepositoryDto repository, List<WorkspaceDto> workspaces) {
            this.repository = repository;
            this.workspaces = workspaces;
        }

        public StepInitializedRepository and() {
            return new StepInitializedRepository(repository);
        }

        public List<WorkspaceDto> get() {
            return workspaces;
        }

        public WorkspaceDto getOrDie(String branch) {
            return get()
                    .stream()
                    .filter(workspace -> Objects.equals(workspace.getBranch(), branch))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("There is no workspace with branch [" + branch + "]."));
        }
    }
}
