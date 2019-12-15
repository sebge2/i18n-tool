package be.sgerard.test.i18n;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleCreationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.test.i18n.support.JsonHolderResultHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
@Component
public class TranslationLocaleTestHelper {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public TranslationLocaleTestHelper(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public Set<TranslationLocaleDto> getLocales() throws Exception {
        final JsonHolderResultHandler<Set<TranslationLocaleDto>> handler = new JsonHolderResultHandler<>(objectMapper, new TypeReference<>() {
        });

        mockMvc
                .perform(request(HttpMethod.GET, "/api/translation/locale/"))
                .andExpect(status().is(OK.value()))
                .andDo(handler);

        return handler.getValue();
    }

    public TranslationLocaleDto createLocale(TranslationLocaleCreationDto creationDto) throws Exception {
        final JsonHolderResultHandler<TranslationLocaleDto> handler = new JsonHolderResultHandler<>(objectMapper, TranslationLocaleDto.class);
        mockMvc
                .perform(
                        request(HttpMethod.POST, "/api/translation/locale/")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(objectMapper.writeValueAsString(creationDto))
                )
                .andExpect(status().is(CREATED.value()))
                .andDo(handler);

        return handler.getValue();
    }

    public TranslationLocaleDto getLocaleByIdOrDie(String id) throws Exception {
        final JsonHolderResultHandler<TranslationLocaleDto> handler = new JsonHolderResultHandler<>(objectMapper, TranslationLocaleDto.class);

        mockMvc
                .perform(request(HttpMethod.GET, "/api/translation/locale/" + id))
                .andExpect(status().is(OK.value()))
                .andDo(handler);

        return handler.getValue();
    }
}
