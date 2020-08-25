package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleCreationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.WithJaneDoeAdminUser;
import be.sgerard.test.i18n.support.WithJohnDoeSimpleUser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.MediaType;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolRepositoryCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frBeWallonLocaleCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frLocaleCreationDto;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TranslationLocaleControllerTest extends AbstractControllerTest {

    @BeforeAll
    public void setupRepo() throws Exception {
        gitRepo
                .createMockFor(i18nToolRepositoryCreationDto())
                .allowAnonymousRead()
                .onCurrentGitProject()
                .create();
    }

    @AfterAll
    public void destroy() {
        gitRepo.destroyAll();
    }

    @Test
    @CleanupDatabase
    @WithJohnDoeSimpleUser
    public void findAllTranslationsNonAdminAllowed() {
        webClient.get()
                .uri("/api/translation/locale/")
                .exchange()
                .expectStatus().isOk()
                .expectBody();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
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
    @CleanupDatabase
    @WithJaneDoeAdminUser
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
    @CleanupDatabase
    @WithJaneDoeAdminUser
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
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void update() {
        final TranslationLocaleDto originalLocale = locale.createLocale(TranslationLocaleCreationDto.builder().language("fr").icon("flag-icon-fr")).get();

        final TranslationLocaleDto updatedLocale = TranslationLocaleDto.builder()
                .id(originalLocale.getId())
                .language("fr")
                .region("BE")
                .variant("wallon")
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
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void updateConflict() {
        final TranslationLocaleDto otherLocale = locale.createLocale(frBeWallonLocaleCreationDto()).get();

        final TranslationLocaleDto currentLocale = locale.createLocale(frLocaleCreationDto()).get();
        final TranslationLocaleDto updatedCurrentLocale = TranslationLocaleDto.builder()
                .id(currentLocale.getId())
                .language(otherLocale.getLanguage())
                .region(otherLocale.getRegion().orElse(null))
                .variants(otherLocale.getVariants())
                .icon(otherLocale.getIcon())
                .build();

        webClient.put()
                .uri("/api/translation/locale/{id}", currentLocale.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedCurrentLocale)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
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

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void deleteForbiddenTranslationsAssociated() {
        final TranslationLocaleDto translationLocale = locale.createLocale(frLocaleCreationDto()).get();

        this.repository
                .create(i18nToolRepositoryCreationDto())
                .initialize()
                .workspaces()
                .workspaceForBranch("master")
                .initialize();

        webClient.delete()
                .uri("/api/translation/locale/{id}", translationLocale.getId())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.messages[0]").isEqualTo("The locale cannot be modified/deleted because some translations are relying on it.");
    }

}
