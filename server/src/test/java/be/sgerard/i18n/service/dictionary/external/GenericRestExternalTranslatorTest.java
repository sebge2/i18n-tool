package be.sgerard.i18n.service.dictionary.external;

import be.sgerard.i18n.model.dictionary.ExternalSourceTranslationRequest;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorGenericRestConfigEntity;
import be.sgerard.i18n.model.support.HttpClientRequest;
import be.sgerard.i18n.service.translator.handler.GenericRestExternalTranslator;
import be.sgerard.i18n.service.support.HttpClient;
import be.sgerard.i18n.service.support.TemplateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static be.sgerard.i18n.model.dictionary.ExternalSourceTranslationRequest.*;
import static be.sgerard.test.i18n.model.TranslationLocaleEntityTestUtils.enLocale;
import static be.sgerard.test.i18n.model.TranslationLocaleEntityTestUtils.frLocale;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenericRestExternalTranslatorTest {

    @Mock
    private TemplateService templateService;

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private GenericRestExternalTranslator service;

    @Test
    public void translateVariablesInQueryParameters() {
        final ExternalTranslatorGenericRestConfigEntity externalSource = createSampleConfig();
        externalSource.withTextQueryParameter("text");
        externalSource.withFromLocaleQueryParameter("source");
        externalSource.withTargetLocaleQueryParameter("target");

        final ExternalSourceTranslationRequest request = new ExternalSourceTranslationRequest(enLocale(), frLocale(), "House");

        final String response = createSampleResponse();

        when(httpClient.execute(any()))
                .thenReturn(Mono.just(response));

        setupTemplateProvider("${" + FROM_LOCALE_VARIABLE + "}", request.toTranslatorParameters(), "en");
        setupTemplateProvider("${" + TARGET_LOCALE_VARIABLE + "}", request.toTranslatorParameters(), "fr");
        setupTemplateProvider("${" + TEXT_VARIABLE + "}", request.toTranslatorParameters(), "House");

        final Flux<String> actual = service.translate(request, externalSource);

        final HttpClientRequest<Object> value = assertHttpClientRequest();

        StepVerifier.create(actual)
                .expectNext("Maison", "Maisons")
                .verifyComplete();

        assertThat(value.getUrl()).isEqualTo("https://my-translator/api/translation");
        assertThat(value.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(value.getParameters()).hasSize(3);
        assertThat(value.getParameters().get("source")).isEqualTo("en");
        assertThat(value.getParameters().get("target")).isEqualTo("fr");
        assertThat(value.getParameters().get("text")).isEqualTo("House");
    }

    @Test
    public void translateVariablesInQueryHeaders() {
        final ExternalTranslatorGenericRestConfigEntity externalSource = createSampleConfig();
        externalSource.withTextQueryHeader("text");
        externalSource.withFromLocaleQueryHeader("source");
        externalSource.withTargetLocaleQueryHeader("target");

        final ExternalSourceTranslationRequest request = new ExternalSourceTranslationRequest(enLocale(), frLocale(), "House");

        final String response = createSampleResponse();

        when(httpClient.execute(any()))
                .thenReturn(Mono.just(response));

        setupTemplateProvider("${" + FROM_LOCALE_VARIABLE + "}", request.toTranslatorParameters(), "en");
        setupTemplateProvider("${" + TARGET_LOCALE_VARIABLE + "}", request.toTranslatorParameters(), "fr");
        setupTemplateProvider("${" + TEXT_VARIABLE + "}", request.toTranslatorParameters(), "House");

        final Flux<String> actual = service.translate(request, externalSource);

        final HttpClientRequest<Object> value = assertHttpClientRequest();

        StepVerifier.create(actual)
                .expectNext("Maison", "Maisons")
                .verifyComplete();

        assertThat(value.getUrl()).isEqualTo("https://my-translator/api/translation");
        assertThat(value.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(value.getHeaders()).hasSize(3);
        assertThat(value.getHeaders().get("source")).isEqualTo("en");
        assertThat(value.getHeaders().get("target")).isEqualTo("fr");
        assertThat(value.getHeaders().get("text")).isEqualTo("House");
    }

    @Test
    public void translateVariablesInBody() {
        final ExternalTranslatorGenericRestConfigEntity externalSource = createSampleConfig();
        final Map<String, String> bodyTemplate = new HashMap<>();
        bodyTemplate.put("source", "${" + FROM_LOCALE_VARIABLE + "}");
        bodyTemplate.put("target", "${" + TARGET_LOCALE_VARIABLE + "}");
        bodyTemplate.put("text", "${" + TEXT_VARIABLE + "}");

        final Map<String, String> bodyResolved = new HashMap<>();
        bodyResolved.put("source", "${" + FROM_LOCALE_VARIABLE + "}");
        bodyResolved.put("target", "${" + TARGET_LOCALE_VARIABLE + "}");
        bodyResolved.put("text", "${" + TEXT_VARIABLE + "}");

        externalSource.setBodyTemplate(toJson(bodyTemplate));

        final ExternalSourceTranslationRequest request = new ExternalSourceTranslationRequest(enLocale(), frLocale(), "House");

        final String response = createSampleResponse();

        when(httpClient.execute(any()))
                .thenReturn(Mono.just(response));

        setupTemplateProviderEscapeJson(externalSource.getBodyTemplate().orElse(null), request.toTranslatorParameters(), toJson(bodyResolved));

        final Flux<String> actual = service.translate(request, externalSource);

        final HttpClientRequest<Object> value = assertHttpClientRequest();

        StepVerifier.create(actual)
                .expectNext("Maison", "Maisons")
                .verifyComplete();

        assertThat(value.getUrl()).isEqualTo("https://my-translator/api/translation");
        assertThat(value.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(value.getBody()).contains(toJson(bodyResolved));
    }

    private ExternalTranslatorGenericRestConfigEntity createSampleConfig() {
        return new ExternalTranslatorGenericRestConfigEntity(
                "My translator",
                "https://my-translator.com",
                HttpMethod.POST,
                "https://my-translator/api/translation",
                "$.data.translations[*].translatedText"
        );
    }

    private String createSampleResponse() {
        return toJson(
                singletonMap(
                        "data",
                        singletonMap(
                                "translations",
                                asList(
                                        singletonMap("translatedText", "Maison"),
                                        singletonMap("translatedText", "Maisons")
                                )
                        )
                )
        );
    }

    private String toJson(Object value) {
        try {
            return new ObjectMapper().findAndRegisterModules().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private void setupTemplateProvider(String template,
                                       Map<String, String> parameters,
                                       String result) {
        final TemplateService.TemplateStep templateStep = mock(TemplateService.TemplateStep.class);

        final TemplateService.TemplateProcessStep processStep1 = mock(TemplateService.TemplateProcessStep.class);
        final TemplateService.TemplateProcessStep processStep2 = mock(TemplateService.TemplateProcessStep.class);
        when(processStep1.withParameters(parameters))
                .thenReturn(processStep2);
        when(processStep2.done())
                .thenReturn(result);

        when(templateStep.process())
                .thenReturn(processStep1);

        when(templateService.newInlineTemplate(template))
                .thenReturn(templateStep);
    }

    private void setupTemplateProviderEscapeJson(String template,
                                                 Map<String, String> parameters,
                                                 String result) {
        final TemplateService.TemplateStep templateStep1 = mock(TemplateService.TemplateStep.class);
        final TemplateService.TemplateStep templateStep2 = mock(TemplateService.TemplateStep.class);
        when(templateStep1.escapeJson())
                .thenReturn(templateStep2);

        final TemplateService.TemplateProcessStep processStep1 = mock(TemplateService.TemplateProcessStep.class);
        final TemplateService.TemplateProcessStep processStep2 = mock(TemplateService.TemplateProcessStep.class);
        when(processStep1.withParameters(parameters))
                .thenReturn(processStep2);
        when(processStep2.done())
                .thenReturn(result);

        when(templateStep2.process())
                .thenReturn(processStep1);

        when(templateService.newInlineTemplate(template))
                .thenReturn(templateStep1);
    }

    @SuppressWarnings("unchecked")
    private HttpClientRequest<Object> assertHttpClientRequest() {
        final ArgumentCaptor<HttpClientRequest<Object>> requestArgumentCaptor = ArgumentCaptor.forClass(HttpClientRequest.class);
        verify(httpClient, times(1))
                .execute(requestArgumentCaptor.capture());

        return requestArgumentCaptor.getValue();
    }
}