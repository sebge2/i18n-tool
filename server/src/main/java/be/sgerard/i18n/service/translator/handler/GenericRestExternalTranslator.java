package be.sgerard.i18n.service.translator.handler;

import be.sgerard.i18n.model.dictionary.ExternalSourceTranslationRequest;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorGenericRestConfigEntity;
import be.sgerard.i18n.model.support.HttpClientRequest;
import be.sgerard.i18n.service.translator.ExternalTranslatorConfigType;
import be.sgerard.i18n.service.support.HttpClient;
import be.sgerard.i18n.service.support.TemplateService;
import com.jayway.jsonpath.JsonPath;
import lombok.AllArgsConstructor;
import net.minidev.json.JSONArray;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * {@link ExternalTranslatorHandler Translator} interacting with the external source using REST.
 *
 * @see ExternalTranslatorGenericRestConfigEntity
 */
@Service
@AllArgsConstructor
public class GenericRestExternalTranslator implements ExternalTranslatorHandler<ExternalTranslatorGenericRestConfigEntity> {

    private final TemplateService templateService;
    private final HttpClient httpClient;

    @Override
    public boolean support(ExternalTranslatorConfigType configType) {
        return configType == ExternalTranslatorConfigType.EXTERNAL_GENERIC_REST;
    }

    @Override
    public Flux<String> translate(ExternalSourceTranslationRequest request, ExternalTranslatorGenericRestConfigEntity config) {
        final Map<String, String> templateParameters = request.toTranslatorParameters();

        final HttpClientRequest<String> httpRequest = HttpClientRequest.newBuilder(config.getMethod(), config.getUrl(), String.class)
                .parameters(mapQueryParams(config, templateParameters))
                .headers(mapQueryHeaders(config, templateParameters))
                .body(mapBody(config, templateParameters))
                .build();

        return httpClient
                .execute(httpRequest)
                .flatMapMany(response -> extractResult(response, config));
    }

    /**
     * Sets up the query parameters as specified by the {@link ExternalTranslatorGenericRestConfigEntity configuration}.
     */
    private Map<String, String> mapQueryParams(ExternalTranslatorGenericRestConfigEntity externalSource,
                                               Map<String, String> templateParameters) {
        return externalSource.getQueryParameters().entrySet().stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        entry -> processTemplatedValue(entry.getValue(), templateParameters)
                ));
    }

    /**
     * Sets up the query headers as specified by the {@link ExternalTranslatorGenericRestConfigEntity configuration}.
     */
    private Map<String, String> mapQueryHeaders(ExternalTranslatorGenericRestConfigEntity externalSource,
                                                Map<String, String> templateParameters) {
        return externalSource.getQueryHeaders().entrySet().stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        entry -> processTemplatedValue(entry.getValue(), templateParameters)
                ));
    }

    /**
     * Sets up the query body as specified by the {@link ExternalTranslatorGenericRestConfigEntity configuration}.
     */
    private String mapBody(ExternalTranslatorGenericRestConfigEntity externalSource,
                           Map<String, String> templateParameters) {
        return externalSource
                .getBodyTemplate()
                .map(body ->
                        templateService
                                .newInlineTemplate(body)
                                .escapeJson()
                                .process()
                                .withParameters(templateParameters)
                                .done()
                )
                .orElse("");
    }

    /**
     * Process the specified value that may contain parameters. The available template parameters are specified.
     */
    private String processTemplatedValue(String value, Map<String, String> templateParameters) {
        return templateService
                .newInlineTemplate(value)
                .process()
                .withParameters(templateParameters)
                .done();
    }

    /**
     * Extracts the HTTP result using the JSON path specified by the {@link ExternalTranslatorGenericRestConfigEntity configuration}.
     */
    private Flux<String> extractResult(String response, ExternalTranslatorGenericRestConfigEntity externalSource) {
        final Object result = JsonPath.read(response, externalSource.getQueryExtractor());

        if (result instanceof JSONArray) {
            return Flux
                    .fromStream(((JSONArray) result).stream())
                    .map(Object::toString);
        } else if (result instanceof String) {
            return Flux.just((String) result);
        } else {
            throw new UnsupportedOperationException("Unsupported result from JSON path [" + result + "].");
        }
    }
}
