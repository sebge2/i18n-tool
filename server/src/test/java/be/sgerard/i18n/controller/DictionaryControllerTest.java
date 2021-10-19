package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.dictionary.dto.DictionaryEntryCreationDto;
import be.sgerard.i18n.model.dictionary.dto.DictionaryEntryPatchDto;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.auth.internal.WithJaneDoeAdminUser;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import java.util.Locale;

import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.enLocaleCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frLocaleCreationDto;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.hasSize;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DictionaryControllerTest extends AbstractControllerTest {

    @BeforeEach
    public void setupLocales() {
        locale
                .createLocale(frLocaleCreationDto()).and()
                .createLocale(enLocaleCreationDto());
    }

    @Nested
    @DisplayName("Create")
    class Create extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void successful() {
            webClient
                    .post()
                    .uri("/api/dictionary")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(
                            DictionaryEntryCreationDto.builder()
                                    .translations(singletonMap(locale.findRegisteredLocale(Locale.ENGLISH).get().getId(), "my term"))
                                    .build()
                    )
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").exists()
                    .jsonPath("$.translations." + locale.findRegisteredLocale(Locale.ENGLISH).get().getId()).isEqualTo("my term");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void invalidLocale() {
            webClient
                    .post()
                    .uri("/api/dictionary")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(
                            DictionaryEntryCreationDto.builder()
                                    .translations(singletonMap("unknown-id", "my term"))
                                    .build()
                    )
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("FindById")
    class FindById extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void successful() {
            final String entryId = createFirstTerm();

            webClient
                    .get()
                    .uri("/api/dictionary/{id}", entryId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(entryId)
                    .jsonPath("$.translations." + locale.findRegisteredLocale(Locale.ENGLISH).get().getId()).isEqualTo("my term")
                    .jsonPath("$.translations." + locale.findRegisteredLocale(Locale.FRENCH).get().getId()).isEqualTo("mon terme");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void invalidId() {
            webClient
                    .get()
                    .uri("/api/dictionary/{id}", "unknown-id")
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    @DisplayName("Find")
    class Find extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void findAll() {
            final String firstEntry = createFirstTerm();
            final String secondEntry = createSecondTerm();

            webClient
                    .get()
                    .uri("/api/dictionary/?sortLocaleId={locale}", locale.findRegisteredLocale(Locale.ENGLISH).get().getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(2))
                    .jsonPath("$[0].id").isEqualTo(firstEntry)
                    .jsonPath("$[0].translations." + locale.findRegisteredLocale(Locale.ENGLISH).get().getId()).isEqualTo("my term")
                    .jsonPath("$[0].translations." + locale.findRegisteredLocale(Locale.FRENCH).get().getId()).isEqualTo("mon terme")
                    .jsonPath("$[1].id").isEqualTo(secondEntry)
                    .jsonPath("$[1].translations." + locale.findRegisteredLocale(Locale.ENGLISH).get().getId()).isEqualTo("my term 2")
                    .jsonPath("$[1].translations." + locale.findRegisteredLocale(Locale.FRENCH).get().getId()).isEqualTo("mon terme 2");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void sortDescending() {
            final String firstEntry = createFirstTerm();
            final String secondEntry = createSecondTerm();

            webClient
                    .get()
                    .uri("/api/dictionary/?sortLocaleId={locale}&sortAscending={ascending}", locale.findRegisteredLocale(Locale.ENGLISH).get().getId(), false)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(2))
                    .jsonPath("$[0].id").isEqualTo(secondEntry)
                    .jsonPath("$[0].translations." + locale.findRegisteredLocale(Locale.ENGLISH).get().getId()).isEqualTo("my term 2")
                    .jsonPath("$[0].translations." + locale.findRegisteredLocale(Locale.FRENCH).get().getId()).isEqualTo("mon terme 2")
                    .jsonPath("$[1].id").isEqualTo(firstEntry)
                    .jsonPath("$[1].translations." + locale.findRegisteredLocale(Locale.ENGLISH).get().getId()).isEqualTo("my term")
                    .jsonPath("$[1].translations." + locale.findRegisteredLocale(Locale.FRENCH).get().getId()).isEqualTo("mon terme");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void searchText() {
            createFirstTerm();
            final String secondEntry = createSecondTerm();

            webClient
                    .get()
                    .uri("/api/dictionary/?text={text}&textLocaleId={locale}", "my term 2", locale.findRegisteredLocale(Locale.ENGLISH).get().getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(1))
                    .jsonPath("$[0].id").isEqualTo(secondEntry)
                    .jsonPath("$[0].translations." + locale.findRegisteredLocale(Locale.ENGLISH).get().getId()).isEqualTo("my term 2")
                    .jsonPath("$[0].translations." + locale.findRegisteredLocale(Locale.FRENCH).get().getId()).isEqualTo("mon terme 2");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void limitLocales() {
            final String firstEntry = createFirstTerm();
            final String secondEntry = createSecondTerm();

            webClient
                    .get()
                    .uri("/api/dictionary/?localeId={locale}&sortLocaleId={sortLocale}", locale.findRegisteredLocale(Locale.ENGLISH).get().getId(), locale.findRegisteredLocale(Locale.ENGLISH).get().getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(2))
                    .jsonPath("$[0].id").isEqualTo(firstEntry)
                    .jsonPath("$[0].translations." + locale.findRegisteredLocale(Locale.ENGLISH).get().getId()).isEqualTo("my term")
                    .jsonPath("$[0].translations." + locale.findRegisteredLocale(Locale.FRENCH).get().getId()).doesNotExist()
                    .jsonPath("$[1].id").isEqualTo(secondEntry)
                    .jsonPath("$[1].translations." + locale.findRegisteredLocale(Locale.ENGLISH).get().getId()).isEqualTo("my term 2")
                    .jsonPath("$[1].translations." + locale.findRegisteredLocale(Locale.FRENCH).get().getId()).doesNotExist();
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void afterLocaleDeletion() {
            final String firstEntry = createFirstTerm();
            final String secondEntry = createSecondTerm();

            final String english = locale.findRegisteredLocale(Locale.ENGLISH).get().getId();
            final String french = locale.findRegisteredLocale(Locale.FRENCH).get().getId();

            locale.findRegisteredLocale(Locale.FRENCH).delete();

            webClient
                    .get()
                    .uri("/api/dictionary/?sortLocaleId={locale}", english)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(2))
                    .jsonPath("$[0].id").isEqualTo(firstEntry)
                    .jsonPath("$[0].translations." + english).isEqualTo("my term")
                    .jsonPath("$[0].translations." + french).doesNotExist()
                    .jsonPath("$[1].id").isEqualTo(secondEntry)
                    .jsonPath("$[1].translations." + english).isEqualTo("my term 2")
                    .jsonPath("$[1].translations." + french).doesNotExist();
        }
    }

    @Nested
    @DisplayName("Update")
    class Update extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void successful() {
            final String entryId = createFirstTerm();

            webClient
                    .patch()
                    .uri("/api/dictionary/{id}", entryId)
                    .bodyValue(
                            DictionaryEntryPatchDto.builder()
                                    .id(entryId)
                                    .translation(locale(Locale.ENGLISH), "my term 2")
                                    .build()
                    )
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(entryId)
                    .jsonPath("$.translations." + locale(Locale.ENGLISH)).isEqualTo("my term 2")
                    .jsonPath("$.translations." + locale(Locale.FRENCH)).isEqualTo("mon terme");
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void invalidIdUnknown() {
            final String entryId = "unknown-id";

            webClient
                    .patch()
                    .uri("/api/dictionary/{id}", entryId)
                    .bodyValue(
                            DictionaryEntryPatchDto.builder()
                                    .id(entryId)
                                    .translation(locale(Locale.ENGLISH), "my term 2")
                                    .build()
                    )
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void invalidLocale() {
            final String entryId = createFirstTerm();

            webClient
                    .patch()
                    .uri("/api/dictionary/{id}", entryId)
                    .bodyValue(
                            DictionaryEntryPatchDto.builder()
                                    .id(entryId)
                                    .translation("unknown-locale-id", "my term 2")
                                    .build()
                    )
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void invalidIdInPatch() {
            final String entryId = createFirstTerm();

            webClient
                    .patch()
                    .uri("/api/dictionary/{id}", entryId)
                    .bodyValue(
                            DictionaryEntryPatchDto.builder()
                                    .id(entryId + "2")
                                    .translation(locale(Locale.ENGLISH), "my term 2")
                                    .build()
                    )
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void invalidIdInUrl() {
            final String entryId = createFirstTerm();

            webClient
                    .patch()
                    .uri("/api/dictionary/{id}", entryId + "2")
                    .bodyValue(
                            DictionaryEntryPatchDto.builder()
                                    .id(entryId)
                                    .translation(locale(Locale.ENGLISH), "my term 2")
                                    .build()
                    )
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("Delete")
    class Delete extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void successful() {
            final String entryId = createFirstTerm();

            webClient
                    .delete()
                    .uri("/api/dictionary/{id}", entryId)
                    .exchange()
                    .expectStatus().isNoContent();
        }

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void invalidId() {
            final String entryId = "unknown-id";

            webClient
                    .delete()
                    .uri("/api/dictionary/{id}", entryId)
                    .exchange()
                    .expectStatus().isNoContent();
        }
    }

    @Nested
    @DisplayName("DeleteAll")
    class DeleteAll extends AbstractControllerTest {

        @Test
        @CleanupDatabase
        @WithJaneDoeAdminUser
        public void successful() {
            createFirstTerm();
            createSecondTerm();

            webClient
                    .delete()
                    .uri("/api/dictionary/")
                    .exchange()
                    .expectStatus().isNoContent();

            webClient
                    .get()
                    .uri("/api/dictionary/")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$").value(hasSize(0));
        }
    }

    private String createFirstTerm() {
        return dictionary
                .createEntry()
                .translation(Locale.ENGLISH, "my term")
                .translation(Locale.FRENCH, "mon terme")
                .save()
                .get()
                .getId();
    }

    private String createSecondTerm() {
        return dictionary
                .createEntry()
                .translation(Locale.ENGLISH, "my term 2")
                .translation(Locale.FRENCH, "mon terme 2")
                .save()
                .get()
                .getId();
    }

    private String locale(Locale locale) {
        return this.locale.findRegisteredLocale(locale).get().getId();
    }
}