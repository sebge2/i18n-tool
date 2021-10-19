package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.dictionary.dto.DictionaryEntryCreationDto;
import be.sgerard.i18n.model.dictionary.dto.DictionaryEntryDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Sebastien Gerard
 */
@Component
public class DictionaryTestHelper {

    private final WebTestClient webClient;
    private final TranslationLocaleTestHelper localeTestHelper;

    public DictionaryTestHelper(WebTestClient webClient, TranslationLocaleTestHelper localeTestHelper) {
        this.webClient = webClient;
        this.localeTestHelper = localeTestHelper;
    }

    public StepCreateEntry createEntry() {
        return new StepCreateEntry();
    }

    public class StepCreateEntry {

        private final Map<String, String> translations = new HashMap<>();

        public StepCreateEntry translation(Locale locale, String translation) {
            return translation(
                    localeTestHelper.findRegisteredLocale(locale).get().getId(),
                    translation
            );
        }

        public StepCreateEntry translation(String localeId, String translation) {
            translations.put(localeId, translation);
            return this;
        }

        public DictionaryTestHelper and() {
            return DictionaryTestHelper.this;
        }

        public StepDictionaryEntry save() {
            final DictionaryEntryCreationDto creationDto = DictionaryEntryCreationDto.builder()
                    .translations(translations)
                    .build();

            final DictionaryEntryDto entry = webClient.post()
                    .uri("/api/dictionary/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(creationDto))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(DictionaryEntryDto.class)
                    .returnResult()
                    .getResponseBody();

            return new StepDictionaryEntry(entry);
        }
    }

    public class StepDictionaryEntry {

        private final DictionaryEntryDto entry;

        public StepDictionaryEntry(DictionaryEntryDto entry) {
            this.entry = entry;
        }

        public DictionaryEntryDto get(){
            return entry;
        }

        public DictionaryTestHelper and() {
            return DictionaryTestHelper.this;
        }
    }
}
