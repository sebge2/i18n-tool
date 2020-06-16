package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleCreationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.test.i18n.support.JsonHolderResultHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

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

    private final AsyncMockMvcTestHelper asyncMvc;
    private final ObjectMapper objectMapper;

    public TranslationLocaleTestHelper(AsyncMockMvcTestHelper asyncMvc, ObjectMapper objectMapper) {
        this.asyncMvc = asyncMvc;
        this.objectMapper = objectMapper;
    }

    public Set<TranslationLocaleDto> getLocales() throws Exception {
        final JsonHolderResultHandler<Set<TranslationLocaleDto>> handler = new JsonHolderResultHandler<>(objectMapper, new TypeReference<>() {
        });

        asyncMvc
                .perform(request(HttpMethod.GET, "/api/translation/locale/"))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().is(OK.value()))
                .andDo(handler);

        return handler.getValue();
    }

    public StepCreatedLocale createLocale(TranslationLocaleCreationDto creationDto) throws Exception {
        final JsonHolderResultHandler<TranslationLocaleDto> handler = new JsonHolderResultHandler<>(objectMapper, TranslationLocaleDto.class);

        asyncMvc
                .perform(
                        request(HttpMethod.POST, "/api/translation/locale/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(creationDto))
                )
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().is(CREATED.value()))
                .andDo(handler);

        return new StepCreatedLocale(handler.getValue());
    }

    public StepCreatedLocale createLocale(TranslationLocaleCreationDto.Builder creationDto) throws Exception {
        return createLocale(creationDto.build());
    }

    public final class StepCreatedLocale {

        private final TranslationLocaleDto locale;

        public StepCreatedLocale(TranslationLocaleDto locale) {
            this.locale = locale;
        }

        public TranslationLocaleTestHelper and() {
            return TranslationLocaleTestHelper.this;
        }

        public TranslationLocaleDto get() {
            return locale;
        }
    }
}
