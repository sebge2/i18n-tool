package be.sgerard.i18n.service.translator.template;

import be.sgerard.i18n.model.translator.dto.GoogleTranslatorConfigDto;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorGenericRestConfigEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * {@link ExternalTranslatorConfigProvider Provider} of the configuration for Google translator.
 */
@Component
public class GoogleTranslatorConfigProvider implements ExternalTranslatorConfigProvider<GoogleTranslatorConfigDto> {

    @Override
    public boolean support(Object request) {
        return request instanceof GoogleTranslatorConfigDto;
    }

    @Override
    public ExternalTranslatorConfigEntity createConfig(GoogleTranslatorConfigDto request) {
        return new ExternalTranslatorGenericRestConfigEntity(
                "Google",
                "https://www.google.com",
                HttpMethod.POST,
                "https://translation.googleapis.com/language/translate/v2",
                "$.data.translations[*].translatedText"
        )
                .withTextQueryParameter("q")
                .withFromLocaleQueryParameter("source")
                .withTargetLocaleQueryParameter("target")
                .withQueryParameter("key", request.getApiKey());
    }
}
