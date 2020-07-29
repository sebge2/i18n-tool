package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.ToolLocale;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.security.user.dto.UserPreferencesDto;
import be.sgerard.test.i18n.support.TransactionalReactiveTest;
import be.sgerard.test.i18n.support.WithJaneDoeAdminUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frLocaleCreationDto;
import static org.hamcrest.Matchers.hasSize;

/**
 * @author Sebastien Gerard
 */
public class UserPreferencesControllerTest extends AbstractControllerTest {

    @Test
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void getUserPreferences() {
        webClient
                .get()
                .uri("/api/user/current/preferences")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @TransactionalReactiveTest
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
            user.resetUserPreferences();
        }
    }

    @Test
    @TransactionalReactiveTest
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
            user.resetUserPreferences();
        }
    }

    @Test
    @TransactionalReactiveTest
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

            locale.deleteLocale(frenchLocale);

            webClient
                    .get()
                    .uri("/api/user/current/preferences")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.preferredLocales").value(hasSize(0));
        } finally {
            user.resetUserPreferences();
        }
    }
}
