package be.sgerard.test.i18n;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.RepositoryDto;
import be.sgerard.test.i18n.support.AsyncMockMvcTestHelper;
import be.sgerard.test.i18n.support.JsonHolderResultHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
@Component
public class RepositoryTestHelper {

    private final AsyncMockMvcTestHelper mockMvc;
    private final ObjectMapper objectMapper;

    public RepositoryTestHelper(AsyncMockMvcTestHelper mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public <R extends RepositoryDto> R createRepository(RepositoryCreationDto creationDto, Class<R> expectedResult) throws Exception {
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

        return resultHandler.getValue();
    }
}
