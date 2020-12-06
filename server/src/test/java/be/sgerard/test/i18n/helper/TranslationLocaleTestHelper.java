package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleCreationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity.toUserString;
import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
@Component
public class TranslationLocaleTestHelper {

    private final WebTestClient webClient;

    public TranslationLocaleTestHelper(WebTestClient webClient) {
        this.webClient = webClient;
    }

    public List<TranslationLocaleDto> getSortedLocales() {
        return getLocales().stream()
                .sorted(Comparator.comparing(locale -> toUserString(locale.getLanguage(), locale.getRegion().orElse(null), locale.getVariants())))
                .collect(toList());
    }

    public List<TranslationLocaleDto> getLocales() {
        return webClient.get()
                .uri("/api/translation/locale/")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TranslationLocaleDto.class)
                .returnResult()
                .getResponseBody();
    }

    public StepLocale findRegisteredLocale(Locale locale) {
        return new StepLocale(
                getLocales()
                .stream()
                .filter(existingLocale -> Objects.equals(existingLocale.toLocale(), locale))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find locale [" + locale + "]."))
        );
    }

    public StepCreatedLocale createLocale(TranslationLocaleCreationDto creationDto) {
        final TranslationLocaleDto locale = webClient.post()
                .uri("/api/translation/locale/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(creationDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TranslationLocaleDto.class)
                .returnResult()
                .getResponseBody();

        return new StepCreatedLocale(locale);
    }

    public StepCreatedLocale createLocale(TranslationLocaleCreationDto.Builder creationDto) {
        return createLocale(creationDto.build());
    }

    public final class StepCreatedLocale {

        private final TranslationLocaleDto locale;

        public StepCreatedLocale(TranslationLocaleDto locale) {
            this.locale = locale;
        }

        public TranslationLocaleTestHelper and() {
            return TranslationLocaleTestHelper.this;
        }

        public TranslationLocaleDto get() {
            return locale;
        }
    }

    public final class StepLocale {

        private final TranslationLocaleDto locale;

        public StepLocale(TranslationLocaleDto locale) {
            this.locale = locale;
        }

        public TranslationLocaleTestHelper and() {
            return TranslationLocaleTestHelper.this;
        }

        public TranslationLocaleDto get() {
            return locale;
        }

        public TranslationLocaleTestHelper delete() {
            webClient.delete()
                    .uri("/api/translation/locale/{id}", locale.getId())
                    .exchange()
                    .expectStatus().isNoContent();

            return TranslationLocaleTestHelper.this;
        }
    }
}
