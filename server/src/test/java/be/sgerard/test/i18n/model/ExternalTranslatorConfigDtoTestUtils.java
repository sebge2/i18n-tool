package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.translator.dto.ExternalTranslatorGenericRestConfigDto;
import org.springframework.http.HttpMethod;

public final class ExternalTranslatorConfigDtoTestUtils {

    private ExternalTranslatorConfigDtoTestUtils() {
    }

    public static ExternalTranslatorGenericRestConfigDto googleTranslatorConfigDto() {
        return ExternalTranslatorGenericRestConfigDto.builder()
                .label("Google")
                .linkUrl("https://www.google.com")
                .method(HttpMethod.POST)
                .url("https://translation.googleapis.com/language/translate/v2")
                .queryExtractor("$.data.translations[*].translatedText")
                .withTextQueryParameter("q")
                .withFromLocaleQueryParameter("source")
                .withTargetLocaleQueryParameter("target")
                .withQueryParameter("key", "N9722i7TMHhwyk67JT4dhRUv")
                .build();
    }

    public static ExternalTranslatorGenericRestConfigDto iTranslateTranslatorConfigDto() {
        return ExternalTranslatorGenericRestConfigDto.builder()
                .label("iTranslate.com")
                .linkUrl("https://www.itranslate.com/")
                .method(HttpMethod.POST)
                .url("https://dev-api.itranslate.com/translation/v2/")
                .queryExtractor("$.target.text")
                .withQueryHeader("Authorization", String.format("Bearer %s", "s6fQ8Dpm35kJ4eEDW7a5aY9K"))
                .withQueryHeader("Content-Type", "application/json")
                .bodyTemplate("{\n" +
                        "    \"source\": {\"dialect\": \"${fromLocale}\", \"text\": \"${text}\"},\n" +
                        "    \"target\": {\"dialect\": \"${targetLocale}\"}\n" +
                        "}")
                .build();
    }

    public static ExternalTranslatorGenericRestConfigDto azureTranslatorConfigDto() {
        return ExternalTranslatorGenericRestConfigDto.builder()
                .label("Azure Translator")
                .linkUrl("https://azure.microsoft.com/en-us/free/cognitive-services/")
                .method(HttpMethod.POST)
                .url("https://api.cognitive.microsofttranslator.com/translate")
                .queryExtractor("$[0].translations[*].text")
                .withQueryParameter("api-version", "3.0")
                .withFromLocaleQueryParameter("from")
                .withTargetLocaleQueryParameter("to")
                .withQueryHeader("Ocp-Apim-Subscription-Key", "ZY27euFJ8ASgcYa54t4jxU22")
                .withQueryHeader("Content-Type", "application/json")
                .withQueryHeader("Ocp-Apim-Subscription-Region", "c3ANj6QQ5nT6aN7V52xXqv4x")
                .bodyTemplate("[{\"Text\":\"${text}\"}]")
                .build();
    }
}
