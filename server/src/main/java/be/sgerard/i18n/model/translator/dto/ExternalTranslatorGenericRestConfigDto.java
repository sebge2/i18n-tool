package be.sgerard.i18n.model.translator.dto;

import be.sgerard.i18n.model.dictionary.ExternalSourceTranslationRequest;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorGenericRestConfigEntity;
import be.sgerard.i18n.model.repository.dto.GitRepositoryDto;
import be.sgerard.i18n.service.translator.ExternalTranslatorConfigType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@Schema(name = "ExternalTranslatorGenericRestConfig", description = "External (not from this tool) source of translation. The source is invoked by a REST call.")
@JsonDeserialize(builder = ExternalTranslatorGenericRestConfigDto.Builder.class)
@Getter
public class ExternalTranslatorGenericRestConfigDto extends ExternalTranslatorConfigDto {

    /**
     * @see ExternalTranslatorConfigType#EXTERNAL_GENERIC_REST
     */
    public static final String TYPE = "EXTERNAL_GENERIC_REST";

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ExternalTranslatorGenericRestConfigDto original) {
        final Builder builder = builder()
                .method(original.getMethod())
                .url(original.getUrl())
                .queryParameters(original.getQueryParameters())
                .queryHeaders(original.getQueryHeaders())
                .bodyTemplate(original.getBodyTemplate())
                .queryExtractor(original.getQueryExtractor());

        fillBuilder(builder, original);

        return builder;
    }

    @Schema(description = "HTTP method used to call the translation API.", required = true)
    private final HttpMethod method;

    @Schema(description = "URL of the translation API.", required = true)
    private final String url;

    @Schema(description = "HTTP query parameters used to call the translation API.", required = true)
    private final Map<String, String> queryParameters;

    @Schema(description = "HTTP header parameters used to call the translation API.", required = true)
    private final Map<String, String> queryHeaders;

    @Schema(description = "JSON body template used to call the translation API.")
    private final String bodyTemplate;

    @Schema(description = "JSON path extracting translations.", required = true)
    private final String queryExtractor;

    private ExternalTranslatorGenericRestConfigDto(Builder builder) {
        super(builder);

        this.method = builder.method;
        this.url = builder.url;
        this.queryParameters = builder.queryParameters;
        this.queryHeaders = builder.queryHeaders;
        this.bodyTemplate = builder.bodyTemplate;
        this.queryExtractor = builder.queryExtractor;
    }

    @Override
    public ExternalTranslatorConfigType getType() {
        return ExternalTranslatorConfigType.EXTERNAL_GENERIC_REST;
    }

    /**
     * Builder of {@link GitRepositoryDto GIT repository DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder extends BaseBuilder<ExternalTranslatorGenericRestConfigDto, Builder> {

        private HttpMethod method;
        private String url;
        private final Map<String, String> queryParameters = new HashMap<>();
        private final Map<String, String> queryHeaders = new HashMap<>();
        private String bodyTemplate;
        private String queryExtractor;

        public Builder() {
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder queryParameters(Map<String, String> queryParameters) {
            this.queryParameters.putAll(queryParameters);
            return this;
        }

        public Builder queryHeaders(Map<String, String> queryHeaders) {
            this.queryHeaders.putAll(queryHeaders);
            return this;
        }

        public Builder bodyTemplate(String bodyTemplate) {
            this.bodyTemplate = bodyTemplate;
            return this;
        }

        public Builder queryExtractor(String queryExtractor) {
            this.queryExtractor = queryExtractor;
            return this;
        }

        /**
         * @see ExternalTranslatorGenericRestConfigEntity#withQueryParameter(java.lang.String, java.lang.String)
         */
        public Builder withQueryParameter(String queryParameter, String value) {
            queryParameters.put(queryParameter, value);
            return this;
        }

        /**
         * @see ExternalTranslatorGenericRestConfigEntity#withTextQueryParameter(String)
         */
        public Builder withTextQueryParameter(String queryParameter) {
            return withQueryParameter(queryParameter, "${" + ExternalSourceTranslationRequest.TEXT_VARIABLE + "}");
        }

        /**
         * @see ExternalTranslatorGenericRestConfigEntity#withFromLocaleQueryParameter(String)
         */
        public Builder withFromLocaleQueryParameter(String queryParameter) {
            return withQueryParameter(queryParameter, "${" + ExternalSourceTranslationRequest.FROM_LOCALE_VARIABLE + "}");
        }

        /**
         * @see ExternalTranslatorGenericRestConfigEntity#withTargetLocaleQueryParameter(String)
         */
        public Builder withTargetLocaleQueryParameter(String queryParameter) {
            return withQueryParameter(queryParameter, "${" + ExternalSourceTranslationRequest.TARGET_LOCALE_VARIABLE + "}");
        }

        /**
         * @see ExternalTranslatorGenericRestConfigEntity#withQueryHeader(String, String)
         */
        public Builder withQueryHeader(String queryHeader, String value) {
            queryHeaders.put(queryHeader, value);
            return this;
        }

        @Override
        public ExternalTranslatorGenericRestConfigDto build() {
            return new ExternalTranslatorGenericRestConfigDto(this);
        }
    }
}
