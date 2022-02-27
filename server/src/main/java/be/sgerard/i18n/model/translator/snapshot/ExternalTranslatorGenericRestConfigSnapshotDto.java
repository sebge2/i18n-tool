package be.sgerard.i18n.model.translator.snapshot;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorGenericRestConfigEntity;
import be.sgerard.i18n.service.translator.ExternalTranslatorConfigType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.Optional;

/**
 * Dto for storing a {@link ExternalTranslatorGenericRestConfigEntity translator config} in a snapshot.
 *
 * @author Sebastien Gerard
 */
@Getter
@SuperBuilder
@Jacksonized
public class ExternalTranslatorGenericRestConfigSnapshotDto extends ExternalTranslatorConfigSnapshotDto {

    /**
     * @see ExternalTranslatorConfigType#EXTERNAL_GENERIC_REST
     */
    public static final String TYPE = "EXTERNAL_GENERIC_REST";

    /**
     * @see ExternalTranslatorGenericRestConfigEntity#getMethod()
     */
    private final HttpMethod method;

    /**
     * @see ExternalTranslatorGenericRestConfigEntity#getUrl()
     */
    private final String url;

    /**
     * @see ExternalTranslatorGenericRestConfigEntity#getQueryParameters()
     */
    private final Map<String, String> queryParameters;

    /**
     * @see ExternalTranslatorGenericRestConfigEntity#getQueryHeaders()
     */
    private final Map<String, String> queryHeaders;

    /**
     * @see ExternalTranslatorGenericRestConfigEntity#getBodyTemplate()
     */
    private final String bodyTemplate;

    /**
     * @see ExternalTranslatorGenericRestConfigEntity#getQueryExtractor()
     */
    private final String queryExtractor;

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
     * Builder of {@link ExternalTranslatorGenericRestConfigSnapshotDto translator config snapshot}.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder extends ExternalTranslatorConfigSnapshotDto.Builder {
    }
}
