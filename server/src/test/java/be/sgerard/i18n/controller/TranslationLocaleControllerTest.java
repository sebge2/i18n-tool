package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleCreationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.test.i18n.support.TransactionalReactiveTest;
import be.sgerard.test.i18n.support.WithInternalUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.MediaType;

import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frBeWallonLocaleCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frLocaleCreationDto;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TranslationLocaleControllerTest extends AbstractControllerTest {

    @Test
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION"})
    public void findAllTranslationsNonAdminAllowed() {
        webClient.get()
                .uri("/api/translation/locale/")
                .exchange()
                .expectStatus().isOk()
                .expectBody();
    }

    @Test
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findAllTranslations() {
        locale.createLocale(frBeWallonLocaleCreationDto());

        webClient.get()
                .uri("/api/translation/locale/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[?(@.language=='fr')]").exists()
                .jsonPath("$[?(@.icon=='flag-icon-fr')]").exists()
                .jsonPath("$[?(@.displayName=='Français (Wallon)')]").exists()
                .jsonPath("$[?(@.region=='BE')]").exists();
    }

    @Test
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void create() {
        final TranslationLocaleCreationDto translationLocale = frBeWallonLocaleCreationDto().build();

        webClient.post()
                .uri("/api/translation/locale/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(translationLocale)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.language").isEqualTo("fr")
                .jsonPath("$.icon").isEqualTo("flag-icon-fr")
                .jsonPath("$.region").isEqualTo("BE")
                .jsonPath("$.variants[0]").isEqualTo("wallon")
                .jsonPath("$.displayName").isEqualTo("Français (Wallon)");
    }

    @Test
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void createTwice() {
        locale.createLocale(frBeWallonLocaleCreationDto().build());

        webClient.post()
                .uri("/api/translation/locale/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(frBeWallonLocaleCreationDto().build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.messages[0]").isEqualTo("There is already an existing locale fr-BE [wallon].");
    }

    @Test
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void update() {
        final TranslationLocaleDto originalLocale = locale.createLocale(TranslationLocaleCreationDto.builder().language("fr").icon("flag-icon-fr")).get();

        final TranslationLocaleDto updatedLocale = TranslationLocaleDto.builder()
                .id(originalLocale.getId())
                .language("fr")
                .region("BE")
                .variants("wallon")
                .icon("flag-icon-fr")
                .displayName("Français (Wallon)")
                .build();

        webClient.put()
                .uri("/api/translation/locale/{id}", originalLocale.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedLocale)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.language").isEqualTo("fr")
                .jsonPath("$.icon").isEqualTo("flag-icon-fr")
                .jsonPath("$.region").isEqualTo("BE")
                .jsonPath("$.variants[0]").isEqualTo("wallon")
                .jsonPath("$.displayName").isEqualTo("Français (Wallon)");
    }

    @Test
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void updateConflict() {
        final TranslationLocaleDto otherLocale = locale.createLocale(frBeWallonLocaleCreationDto()).get();

        final TranslationLocaleDto currentLocale = locale.createLocale(frLocaleCreationDto()).get();
        final TranslationLocaleDto updatedCurrentLocale = TranslationLocaleDto.builder()
                .id(currentLocale.getId())
                .language(otherLocale.getLanguage())
                .region(otherLocale.getRegion().orElse(null))
                .variants(otherLocale.getVariants())
                .build();

        webClient.put()
                .uri("/api/translation/locale/{id}", currentLocale.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedCurrentLocale)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void delete() {
        final TranslationLocaleDto translationLocale = locale.createLocale(frBeWallonLocaleCreationDto()).get();

        final int numberLocalesBefore = locale.getLocales().size();

        webClient.delete()
                .uri("/api/translation/locale/{id}", translationLocale.getId())
                .exchange()
                .expectStatus().isNoContent();

        final int numberLocalesAfter = locale.getLocales().size();

        assertThat(numberLocalesAfter).isEqualTo(numberLocalesBefore - 1);
    }

}
