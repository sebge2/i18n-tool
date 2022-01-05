package be.sgerard.i18n.service.translator.template;

import be.sgerard.i18n.model.translator.dto.ITranslateTranslatorConfigDto;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorGenericRestConfigEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * {@link ExternalTranslatorConfigProvider Provider} of configuration for using iTranslate.com API.
 *
 * <a href="https://itranslate.com/itranslate-translation-api-documentation">Doc</a>
 */
@Component
public class ITranslateTranslatorConfigProvider implements ExternalTranslatorConfigProvider<ITranslateTranslatorConfigDto> {

    @Override
    public boolean support(Object request) {
        return request instanceof ITranslateTranslatorConfigDto;
    }

    @Override
    public ExternalTranslatorConfigEntity createConfig(ITranslateTranslatorConfigDto request) {
        return new ExternalTranslatorGenericRestConfigEntity(
                "iTranslate.com",
                "https://www.itranslate.com/",
                HttpMethod.POST,
                "https://dev-api.itranslate.com/translation/v2/",
                "$.target.text"
        )
                .withQueryHeader("Authorization", String.format("Bearer %s", request.getBearerToken()))
                .withQueryHeader("Content-Type", "application/json")
                .setBodyTemplate("{\n" +
                        "    \"source\": {\"dialect\": \"${fromLocale}\", \"text\": \"${text}\"},\n" +
                        "    \"target\": {\"dialect\": \"${targetLocale}\"}\n" +
                        "}")
                ;
    }
}
