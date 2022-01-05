package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.translator.dto.ExternalTranslatorConfigDto;
import be.sgerard.i18n.model.translator.dto.ExternalTranslatorGenericRestConfigDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static be.sgerard.test.i18n.model.ExternalTranslatorConfigDtoTestUtils.*;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

@Component
public class ExternalTranslatorTestHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

    private final WebTestClient webClient;
    private final HttpClientTestHelper httpClient;

    public ExternalTranslatorTestHelper(WebTestClient webClient, HttpClientTestHelper httpClient) {
        this.webClient = webClient;
        this.httpClient = httpClient;
    }

    public GoogleExternalTranslatorConfigStep createGoogleTranslatorConfig() {
        return new GoogleExternalTranslatorConfigStep(createAndGetDto(googleTranslatorConfigDto()));
    }

    public GenericExternalTranslatorConfigStep createITranslateTranslatorConfig() {
        return create(iTranslateTranslatorConfigDto());
    }

    public GenericExternalTranslatorConfigStep createAzureTranslatorConfig() {
        return create(azureTranslatorConfigDto());
    }

    private String toJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public GenericExternalTranslatorConfigStep create(ExternalTranslatorGenericRestConfigDto config) {
        final ExternalTranslatorGenericRestConfigDto response = createAndGetDto(config);

        return new GenericExternalTranslatorConfigStep(response);
    }

    private ExternalTranslatorGenericRestConfigDto createAndGetDto(ExternalTranslatorGenericRestConfigDto config) {
        return webClient
                .post()
                .uri("/api/external-translator")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(config)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ExternalTranslatorGenericRestConfigDto.class)
                .returnResult()
                .getResponseBody();
    }

    public List<ExternalTranslatorConfigDto> getConfigurations() {
        return webClient.get()
                .uri("/api/external-translator")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ExternalTranslatorConfigDto.class)
                .returnResult()
                .getResponseBody();
    }

    public class GenericExternalTranslatorConfigStep {

        private final ExternalTranslatorGenericRestConfigDto config;

        private GenericExternalTranslatorConfigStep(ExternalTranslatorGenericRestConfigDto config) {
            this.config = config;
        }

        public ExternalTranslatorGenericRestConfigDto get() {
            return config;
        }

        public ExternalTranslatorTestHelper and() {
            return ExternalTranslatorTestHelper.this;
        }
    }

    public abstract class MockableGenericExternalTranslatorConfigStep extends GenericExternalTranslatorConfigStep {

        protected MockableGenericExternalTranslatorConfigStep(ExternalTranslatorGenericRestConfigDto config) {
            super(config);
        }

        @SuppressWarnings("UnusedReturnValue")
        public abstract GenericExternalTranslatorConfigStep withTranslation(Locale fromLocale, String text, Locale targetLocale, String translation);
    }

    public class GoogleExternalTranslatorConfigStep extends MockableGenericExternalTranslatorConfigStep {

        private GoogleExternalTranslatorConfigStep(ExternalTranslatorGenericRestConfigDto config) {
            super(config);
        }

        @Override
        public GoogleExternalTranslatorConfigStep withTranslation(Locale fromLocale, String text, Locale targetLocale, String translation) {
            final Map<String, Map<String, List<Map<String, String>>>> expectedPayload = singletonMap(
                    "data",
                    singletonMap(
                            "translations",
                            singletonList(
                                    singletonMap("translatedText", translation)
                            )
                    )
            );

            final Map<String, String> queryParameters = new HashMap<>();
            queryParameters.put("source", fromLocale.toString());
            queryParameters.put("target", targetLocale.toString());
            queryParameters.put("q", text);
            queryParameters.put("key", get().getQueryParameters().get("key"));

            httpClient
                    .mockRequest()
                    .methodEqualsTo(get().getMethod())
                    .urlMatchingExactly(get().getUrl())
                    .parametersContainingExactly(queryParameters)
                    .answerValue(toJson(expectedPayload));

            return this;
        }
    }
}
