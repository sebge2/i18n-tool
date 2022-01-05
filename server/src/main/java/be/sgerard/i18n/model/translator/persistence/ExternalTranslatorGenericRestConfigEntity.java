package be.sgerard.i18n.model.translator.persistence;

import be.sgerard.i18n.model.dictionary.ExternalSourceTranslationRequest;
import be.sgerard.i18n.service.translator.ExternalTranslatorConfigType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpMethod;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * External (not from this tool) source of translation. The source is invoked by a REST call.
 *
 * @author Sebastien Gerard
 */
@Document("external_translator_config")
@TypeAlias("generic_rest")
@Getter
@Setter
@Accessors(chain = true)
public class ExternalTranslatorGenericRestConfigEntity extends ExternalTranslatorConfigEntity {

    /**
     * HTTP method used to call the translation API.
     */
    @NotNull
    private HttpMethod method;

    /**
     * URL of the translation API.
     */
    @NotNull
    private String url;

    /**
     * HTTP query parameters used to call the translation API.
     */
    @NotNull
    private Map<String, String> queryParameters = new HashMap<>();

    /**
     * HTTP header parameters used to call the translation API.
     */
    @NotNull
    private Map<String, String> queryHeaders = new HashMap<>();

    /**
     * JSON body template used to call the translation API.
     */
    private String bodyTemplate;

    /**
     * JSON path extracting translations.
     */
    @NotNull
    private String queryExtractor;

    @PersistenceConstructor
    @SuppressWarnings("unused")
    public ExternalTranslatorGenericRestConfigEntity() {
    }

    public ExternalTranslatorGenericRestConfigEntity(String label,
                                                     String linkUrl,
                                                     HttpMethod method,
                                                     String url,
                                                     String queryExtractor) {
        super(label, linkUrl);

        this.method = method;
        this.url = url;
        this.queryExtractor = queryExtractor;
    }

    @Override
    public ExternalTranslatorConfigType getType() {
        return ExternalTranslatorConfigType.EXTERNAL_GENERIC_REST;
    }

    /**
     * @see #bodyTemplate
     */
    public Optional<String> getBodyTemplate() {
        return Optional.ofNullable(bodyTemplate);
    }

    /**
     * Adds a query parameter having the specified name and value. The value may be tokenized with "${parameterName}.
     */
    public ExternalTranslatorGenericRestConfigEntity withQueryParameter(String queryParameter, String value) {
        getQueryParameters().put(queryParameter, value);
        return this;
    }

    /**
     * Adds a query parameter having the specified name for which the value will be the text to translate.
     */
    public ExternalTranslatorGenericRestConfigEntity withTextQueryParameter(String queryParameter) {
        return withQueryParameter(queryParameter, "${" + ExternalSourceTranslationRequest.TEXT_VARIABLE + "}");
    }

    /**
     * Adds a query parameter having the specified name for which the value will be the original locale.
     */
    public ExternalTranslatorGenericRestConfigEntity withFromLocaleQueryParameter(String queryParameter) {
        return withQueryParameter(queryParameter, "${" + ExternalSourceTranslationRequest.FROM_LOCALE_VARIABLE + "}");
    }

    /**
     * Adds a query parameter having the specified name for which the value will be the locale to translate to.
     */
    public ExternalTranslatorGenericRestConfigEntity withTargetLocaleQueryParameter(String queryParameter) {
        return withQueryParameter(queryParameter, "${" + ExternalSourceTranslationRequest.TARGET_LOCALE_VARIABLE + "}");
    }

    /**
     * Adds a query header having the specified name and value. The value may be tokenized with "${parameterName}.
     */
    public ExternalTranslatorGenericRestConfigEntity withQueryHeader(String queryHeader, String value) {
        getQueryHeaders().put(queryHeader, value);
        return this;
    }

    /**
     * Adds a query header having the specified name for which the value will be the text to translate.
     */
    public ExternalTranslatorGenericRestConfigEntity withTextQueryHeader(String queryParameter) {
        return withQueryHeader(queryParameter, "${" + ExternalSourceTranslationRequest.TEXT_VARIABLE + "}");
    }

    /**
     * Adds a query header having the specified name for which the value will be the original locale.
     */
    public ExternalTranslatorGenericRestConfigEntity withFromLocaleQueryHeader(String queryParameter) {
        return withQueryHeader(queryParameter, "${" + ExternalSourceTranslationRequest.FROM_LOCALE_VARIABLE + "}");
    }

    /**
     * Adds a query header having the specified name for which the value will be the locale to translate to.
     */
    public ExternalTranslatorGenericRestConfigEntity withTargetLocaleQueryHeader(String queryParameter) {
        return withQueryHeader(queryParameter, "${" + ExternalSourceTranslationRequest.TARGET_LOCALE_VARIABLE + "}");
    }
}
