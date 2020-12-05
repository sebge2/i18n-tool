package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.ToolLocale;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.user.dto.UserPreferencesDto;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.auth.internal.WithJaneDoeAdminUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frLocaleCreationDto;
import static org.hamcrest.Matchers.hasSize;

/**
 * @author Sebastien Gerard
 */
public class UserPreferencesControllerTest extends AbstractControllerTest {

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void getUserPreferences() {
        webClient
                .get()
                .uri("/api/user/current/preferences")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void updateUserPreferences() {
        final TranslationLocaleDto frenchLocale = locale.createLocale(frLocaleCreationDto()).get();

        try {
            webClient
                    .put()
                    .uri("/api/user/current/preferences")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(
                            UserPreferencesDto.builder()
                                    .toolLocale(ToolLocale.FRENCH)
                                    .preferredLocales(frenchLocale.getId())
                                    .build()
                    )
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.toolLocale").isEqualTo(ToolLocale.FRENCH.name())
                    .jsonPath("$.preferredLocales[0]").isEqualTo(frenchLocale.getId());

            webClient
                    .get()
                    .uri("/api/user/current/preferences")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.toolLocale").isEqualTo(ToolLocale.FRENCH.name())
                    .jsonPath("$.preferredLocales[0]").isEqualTo(frenchLocale.getId());
        } finally {
            user.currentUser().resetPreferences();
        }
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void updateUserPreferencesMissingLocale() {
        try {
            webClient
                    .put()
                    .uri("/api/user/current/preferences")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(
                            UserPreferencesDto.builder()
                                    .toolLocale(ToolLocale.FRENCH)
                                    .preferredLocales("unknown")
                                    .build()
                    )
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.messages[0]").isEqualTo("The locale [unknown] cannot be found.");
        } finally {
            user.currentUser().resetPreferences();
        }
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void getUserPreferencesAfterLocaleDeletion() {
        final TranslationLocaleDto frenchLocale = locale.createLocale(frLocaleCreationDto()).get();

        try {
            webClient
                    .put()
                    .uri("/api/user/current/preferences")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(
                            UserPreferencesDto.builder()
                                    .preferredLocales(frenchLocale.getId())
                                    .build()
                    )
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.preferredLocales[0]").isEqualTo(frenchLocale.getId());

            locale.findRegisteredLocale(frenchLocale.toLocale()).delete();

            webClient
                    .get()
                    .uri("/api/user/current/preferences")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.preferredLocales").value(hasSize(0));
        } finally {
            user.currentUser().resetPreferences();
        }
    }
}
