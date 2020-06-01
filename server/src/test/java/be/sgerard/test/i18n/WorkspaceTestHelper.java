package be.sgerard.test.i18n;

import be.sgerard.i18n.model.workspace.WorkspaceDto;
import be.sgerard.test.i18n.support.AsyncMockMvcTestHelper;
import be.sgerard.test.i18n.support.JsonHolderResultHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
@Component
public class WorkspaceTestHelper {

    private final AsyncMockMvcTestHelper mockMvc;
    private final ObjectMapper objectMapper;

    public WorkspaceTestHelper(AsyncMockMvcTestHelper mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public List<WorkspaceDto> sync(String repositoryId) throws Exception {
        final JsonHolderResultHandler<List<WorkspaceDto>> resultHandler = new JsonHolderResultHandler<>(objectMapper, new TypeReference<>() {
        });

        mockMvc
                .perform(post("/api/repository/{id}/workspace/do?action=SYNCHRONIZE", repositoryId))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andDo(resultHandler);

        return resultHandler.getValue();
    }
}
