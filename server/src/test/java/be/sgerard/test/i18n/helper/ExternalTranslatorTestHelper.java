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
import static org.apache.commons.text.StringEscapeUtils.escapeJson;

@Component
public class ExternalTranslatorTestHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

    private final WebTestClient webClient;
    private final HttpClientTestHelper httpClient;

    public ExternalTranslatorTestHelper(WebTestClient webClient, HttpClientTestHelper httpClient) {
        this.webClient = webClient;
        this.httpClient = httpClient;
    }

    public GoogleTranslatorStep googleTranslator() {
        return new GoogleTranslatorStep(googleTranslatorConfigDto());
    }

    public ITranslateTranslatorStep iTranslate() {
        return new ITranslateTranslatorStep(iTranslateTranslatorConfigDto());
    }

    public AzureTranslatorStep azure() {
        return new AzureTranslatorStep(azureTranslatorConfigDto());
    }

    private String toJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
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

    public abstract class TranslatorStep<S extends TranslatorStep<S>> {

        private final ExternalTranslatorGenericRestConfigDto config;

        private TranslatorStep(ExternalTranslatorGenericRestConfigDto config) {
            this.config = config;

            withTestTranslation();
        }

        @SuppressWarnings("UnusedReturnValue")
        public abstract S withTranslation(Locale fromLocale, String text, Locale targetLocale, String translation);

        public abstract SavedTranslatorConfigStep createConfig();

        public ExternalTranslatorTestHelper and() {
            return ExternalTranslatorTestHelper.this;
        }

        protected ExternalTranslatorGenericRestConfigDto get() {
            return config;
        }

        @SuppressWarnings("UnusedReturnValue")
        private S withTestTranslation() {
            return withTranslation(Locale.ENGLISH, "test", Locale.FRENCH, "test");
        }
    }

    public class GoogleTranslatorStep extends TranslatorStep<GoogleTranslatorStep> {

        private GoogleTranslatorStep(ExternalTranslatorGenericRestConfigDto config) {
            super(config);
        }

        @Override
        public GoogleTranslatorStep withTranslation(Locale fromLocale, String text, Locale targetLocale, String translation) {
            final Map<String, Map<String, List<Map<String, String>>>> result = singletonMap(
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
                    .answerValue(toJson(result));

            return this;
        }

        @Override
        public SavedTranslatorConfigStep createConfig() {
            return new SavedTranslatorConfigStep(createAndGetDto(get()), this);
        }
    }

    public class ITranslateTranslatorStep extends TranslatorStep<ITranslateTranslatorStep> {

        private ITranslateTranslatorStep(ExternalTranslatorGenericRestConfigDto config) {
            super(config);
        }

        @Override
        public ITranslateTranslatorStep withTranslation(Locale fromLocale, String text, Locale targetLocale, String translation) {
            final Map<String, Map<String, String>> result = singletonMap("target", singletonMap("text", translation));

            final Map<String, String> queryHeaders = new HashMap<>();
            queryHeaders.put("Authorization", String.format("Bearer %s", "s6fQ8Dpm35kJ4eEDW7a5aY9K"));
            queryHeaders.put("Content-Type", "application/json");

            httpClient
                    .mockRequest()
                    .methodEqualsTo(get().getMethod())
                    .urlMatchingExactly(get().getUrl())
                    .headersContainingExactly(queryHeaders)
                    .bodyMatchingExactly(String.format(
                            "{\n" +
                                    "    \"source\": {\"dialect\": \"%s\", \"text\": \"%s\"},\n" +
                                    "    \"target\": {\"dialect\": \"%s\"}\n" +
                                    "}",
                            escapeJson(fromLocale.toString()),
                            escapeJson(text),
                            escapeJson(targetLocale.toString())
                    ))
                    .answerValue(toJson(result));

            return this;
        }

        @Override
        public SavedTranslatorConfigStep createConfig() {
            return new SavedTranslatorConfigStep(createAndGetDto(get()), this);
        }
    }

    public class AzureTranslatorStep extends TranslatorStep<AzureTranslatorStep> {

        private AzureTranslatorStep(ExternalTranslatorGenericRestConfigDto config) {
            super(config);
        }

        @Override
        public AzureTranslatorStep withTranslation(Locale fromLocale, String text, Locale targetLocale, String translation) {
            final List<Map<String, String>> result = singletonList(singletonMap("translations", text));

            final Map<String, String> queryParameters = new HashMap<>();
            queryParameters.put("api-version", "3.0");
            queryParameters.put("from", fromLocale.toString());
            queryParameters.put("to", targetLocale.toString());

            final Map<String, String> queryHeaders = new HashMap<>();
            queryHeaders.put("Ocp-Apim-Subscription-Key", "ZY27euFJ8ASgcYa54t4jxU22");
            queryHeaders.put("Content-Type", "application/json");
            queryHeaders.put("Ocp-Apim-Subscription-Region", "c3ANj6QQ5nT6aN7V52xXqv4x");

            httpClient
                    .mockRequest()
                    .methodEqualsTo(get().getMethod())
                    .urlMatchingExactly(get().getUrl())
                    .headersContainingExactly(queryHeaders)
                    .parametersContainingExactly(queryParameters)
                    .bodyMatchingExactly(String.format(
                            "[{\"Text\":\"%s\"}]",
                            escapeJson(text)
                    ))
                    .answerValue(toJson(result));

            return this;
        }

        @Override
        public SavedTranslatorConfigStep createConfig() {
            return new SavedTranslatorConfigStep(createAndGetDto(get()), this);
        }
    }

    public class SavedTranslatorConfigStep {

        private final ExternalTranslatorGenericRestConfigDto config;
        private final TranslatorStep<?> translatorStep;

        private SavedTranslatorConfigStep(ExternalTranslatorGenericRestConfigDto config, TranslatorStep<?> translatorStep) {
            this.config = config;
            this.translatorStep = translatorStep;
        }

        public ExternalTranslatorGenericRestConfigDto get() {
            return config;
        }

        public ExternalTranslatorTestHelper and() {
            return ExternalTranslatorTestHelper.this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public SavedTranslatorConfigStep withTranslation(Locale fromLocale, String text, Locale targetLocale, String translation) {
            translatorStep.withTranslation(fromLocale, text, targetLocale, translation);
            return this;
        }
    }
}
