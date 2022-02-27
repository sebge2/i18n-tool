package be.sgerard.i18n.service.translator.template;

import be.sgerard.i18n.model.translator.dto.AzureTranslatorConfigDto;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorGenericRestConfigEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * {@link ExternalTranslatorConfigProvider Provider} of configuration for using Microsoft Azure Translator API.
 *
 * <a href="https://docs.microsoft.com/en-us/azure/cognitive-services/translator/reference/v3-0-translate">Doc</a>
 */
@Component
public class AzureTranslatorConfigProvider implements ExternalTranslatorConfigProvider<AzureTranslatorConfigDto> {

    @Override
    public boolean support(Object request) {
        return request instanceof AzureTranslatorConfigDto;
    }

    @Override
    public ExternalTranslatorConfigEntity createConfig(AzureTranslatorConfigDto request) {
        return new ExternalTranslatorGenericRestConfigEntity(
                "Azure Translator",
                "https://azure.microsoft.com/en-us/free/cognitive-services/",
                HttpMethod.POST,
                "https://api.cognitive.microsofttranslator.com/translate",
                "$[0].translations[*].text"
        )
                .withQueryParameter("api-version", "3.0")
                .withFromLocaleQueryParameter("from")
                .withTargetLocaleQueryParameter("to")
                .withQueryHeader("Ocp-Apim-Subscription-Key", request.getSubscriptionKey())
                .withQueryHeader("Content-Type", "application/json")
                .withQueryHeader("Ocp-Apim-Subscription-Region", request.getSubscriptionRegion())
                .setBodyTemplate("[{\"Text\":\"${text}\"}]");
    }
}
