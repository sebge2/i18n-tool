package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.translator.dto.*;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.auth.internal.WithJaneDoeAdminUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static be.sgerard.test.i18n.model.ExternalTranslatorConfigDtoTestUtils.googleTranslatorConfigDto;
import static org.hamcrest.Matchers.hasSize;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExternalTranslatorControllerTest extends AbstractControllerTest {

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void findAll() {
        externalTranslator.googleTranslator().createConfig();

        webClient
                .get()
                .uri("/api/external-translator")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").value(hasSize(1));
    }

    @Nested
    @DisplayName("findById")
    class FindById extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void successful() {
            final ExternalTranslatorConfigDto config = externalTranslator.googleTranslator().createConfig().get();

            webClient
                    .get()
                    .uri("/api/external-translator/{id}", config.getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.label").isEqualTo("Google")
                    .jsonPath("$.linkUrl").isEqualTo("https://www.google.com")
                    .jsonPath("$.method").isEqualTo("POST")
                    .jsonPath("$.url").isEqualTo("https://translation.googleapis.com/language/translate/v2")
                    .jsonPath("$.queryExtractor").isEqualTo("$.data.translations[*].translatedText")
                    .jsonPath("$.queryParameters.q").isEqualTo("${text}")
                    .jsonPath("$.queryParameters.source").isEqualTo("${fromLocale}")
                    .jsonPath("$.queryParameters.key").isEqualTo("N9722i7TMHhwyk67JT4dhRUv")
                    .jsonPath("$.queryParameters.target").isEqualTo("${targetLocale}")
                    .jsonPath("$.queryHeaders").isEmpty()
                    .jsonPath("$.bodyTemplate").isEmpty()
                    .jsonPath("$.type").isEqualTo("EXTERNAL_GENERIC_REST");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void notFound() {
            externalTranslator.googleTranslator().createConfig().get(); // noise

            webClient
                    .get()
                    .uri("/api/external-translator/{id}", "another")
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    @DisplayName("create")
    class Create extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void fromScratch() {
            webClient
                    .post()
                    .uri("/api/external-translator")
                    .bodyValue(googleTranslatorConfigDto())
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.label").isEqualTo("Google")
                    .jsonPath("$.linkUrl").isEqualTo("https://www.google.com")
                    .jsonPath("$.method").isEqualTo("POST")
                    .jsonPath("$.url").isEqualTo("https://translation.googleapis.com/language/translate/v2")
                    .jsonPath("$.queryExtractor").isEqualTo("$.data.translations[*].translatedText")
                    .jsonPath("$.queryParameters.q").isEqualTo("${text}")
                    .jsonPath("$.queryParameters.source").isEqualTo("${fromLocale}")
                    .jsonPath("$.queryParameters.key").isEqualTo("N9722i7TMHhwyk67JT4dhRUv")
                    .jsonPath("$.queryParameters.target").isEqualTo("${targetLocale}")
                    .jsonPath("$.queryHeaders").isEmpty()
                    .jsonPath("$.bodyTemplate").isEmpty()
                    .jsonPath("$.type").isEqualTo("EXTERNAL_GENERIC_REST");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void iTranslate() {
            externalTranslator.iTranslate();

            webClient
                    .post()
                    .uri("/api/external-translator/iTranslate")
                    .bodyValue(new ITranslateTranslatorConfigDto("s6fQ8Dpm35kJ4eEDW7a5aY9K"))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.label").isEqualTo("iTranslate.com")
                    .jsonPath("$.linkUrl").isEqualTo("https://www.itranslate.com/")
                    .jsonPath("$.method").isEqualTo("POST")
                    .jsonPath("$.url").isEqualTo("https://dev-api.itranslate.com/translation/v2/")
                    .jsonPath("$.queryExtractor").isEqualTo("$.target.text")
                    .jsonPath("$.queryHeaders.Authorization").isEqualTo("Bearer s6fQ8Dpm35kJ4eEDW7a5aY9K")
                    .jsonPath("$.queryHeaders.Content-Type").isEqualTo("application/json")
                    .jsonPath("$.queryParameters").isEmpty()
                    .jsonPath("$.bodyTemplate").isEqualTo("{\n    \"source\": {\"dialect\": \"${fromLocale}\", \"text\": \"${text}\"},\n    \"target\": {\"dialect\": \"${targetLocale}\"}\n}")
                    .jsonPath("$.type").isEqualTo("EXTERNAL_GENERIC_REST");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void googleTranslate() {
            externalTranslator.googleTranslator();

            webClient
                    .post()
                    .uri("/api/external-translator/google")
                    .bodyValue(new GoogleTranslatorConfigDto("N9722i7TMHhwyk67JT4dhRUv"))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.label").isEqualTo("Google")
                    .jsonPath("$.linkUrl").isEqualTo("https://www.google.com")
                    .jsonPath("$.method").isEqualTo("POST")
                    .jsonPath("$.url").isEqualTo("https://translation.googleapis.com/language/translate/v2")
                    .jsonPath("$.queryExtractor").isEqualTo("$.data.translations[*].translatedText")
                    .jsonPath("$.queryParameters.q").isEqualTo("${text}")
                    .jsonPath("$.queryParameters.source").isEqualTo("${fromLocale}")
                    .jsonPath("$.queryParameters.key").isEqualTo("N9722i7TMHhwyk67JT4dhRUv")
                    .jsonPath("$.queryParameters.target").isEqualTo("${targetLocale}")
                    .jsonPath("$.queryHeaders").isEmpty()
                    .jsonPath("$.bodyTemplate").isEmpty()
                    .jsonPath("$.type").isEqualTo("EXTERNAL_GENERIC_REST");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void azureTranslator() {
            externalTranslator.azure();

            webClient
                    .post()
                    .uri("/api/external-translator/azure")
                    .bodyValue(new AzureTranslatorConfigDto("ZY27euFJ8ASgcYa54t4jxU22", "c3ANj6QQ5nT6aN7V52xXqv4x"))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.label").isEqualTo("Azure Translator")
                    .jsonPath("$.linkUrl").isEqualTo("https://azure.microsoft.com/en-us/free/cognitive-services/")
                    .jsonPath("$.method").isEqualTo("POST")
                    .jsonPath("$.url").isEqualTo("https://api.cognitive.microsofttranslator.com/translate")
                    .jsonPath("$.queryExtractor").isEqualTo("$[0].translations[*].text")
                    .jsonPath("$.queryParameters.api-version").isEqualTo("3.0")
                    .jsonPath("$.queryParameters.from").isEqualTo("${fromLocale}")
                    .jsonPath("$.queryParameters.to").isEqualTo("${targetLocale}")
                    .jsonPath("$.queryHeaders.Ocp-Apim-Subscription-Key").isEqualTo("ZY27euFJ8ASgcYa54t4jxU22")
                    .jsonPath("$.queryHeaders.Ocp-Apim-Subscription-Region").isEqualTo("c3ANj6QQ5nT6aN7V52xXqv4x")
                    .jsonPath("$.queryHeaders.Content-Type").isEqualTo("application/json")
                    .jsonPath("$.bodyTemplate").isEqualTo("[{\"Text\":\"${text}\"}]")
                    .jsonPath("$.type").isEqualTo("EXTERNAL_GENERIC_REST");
        }
    }

    @Nested
    @DisplayName("update")
    class Update extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void genericRest() {
            final ExternalTranslatorGenericRestConfigDto config = externalTranslator.googleTranslator().createConfig().get();
            final ExternalTranslatorGenericRestConfigDto updated = ExternalTranslatorGenericRestConfigDto.builder(config)
                    .label("another")
                    .build();

            webClient
                    .put()
                    .uri("/api/external-translator/{id}", config.getId())
                    .bodyValue(updated)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.label").isEqualTo("another")
                    .jsonPath("$.linkUrl").isEqualTo("https://www.google.com")
                    .jsonPath("$.method").isEqualTo("POST")
                    .jsonPath("$.url").isEqualTo("https://translation.googleapis.com/language/translate/v2")
                    .jsonPath("$.queryExtractor").isEqualTo("$.data.translations[*].translatedText")
                    .jsonPath("$.queryParameters.q").isEqualTo("${text}")
                    .jsonPath("$.queryParameters.source").isEqualTo("${fromLocale}")
                    .jsonPath("$.queryParameters.key").isEqualTo("N9722i7TMHhwyk67JT4dhRUv")
                    .jsonPath("$.queryParameters.target").isEqualTo("${targetLocale}")
                    .jsonPath("$.queryHeaders").isEmpty()
                    .jsonPath("$.bodyTemplate").isEmpty()
                    .jsonPath("$.type").isEqualTo("EXTERNAL_GENERIC_REST");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void failedWrongId() {
            final ExternalTranslatorConfigDto config = externalTranslator.googleTranslator().createConfig().get();

            webClient
                    .put()
                    .uri("/api/external-translator/{id}", "another")
                    .bodyValue(config)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void successful() {
            final ExternalTranslatorConfigDto config = externalTranslator.googleTranslator().createConfig().get();

            webClient
                    .delete()
                    .uri("/api/external-translator/{id}", config.getId())
                    .exchange()
                    .expectStatus().isNoContent();
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void notFound() {
            externalTranslator.googleTranslator().createConfig(); // noise

            webClient
                    .delete()
                    .uri("/api/external-translator/{id}", "another")
                    .exchange()
                    .expectStatus().isNoContent();
        }
    }
}