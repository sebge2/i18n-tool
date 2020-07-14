package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.ToolLocale;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.security.user.dto.UserPreferencesDto;
import be.sgerard.i18n.service.user.UserManager;
import be.sgerard.test.i18n.support.TransactionalReactiveTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frLocaleCreationDto;

/**
 * @author Sebastien Gerard
 */
public class UserPreferencesControllerTest extends AbstractControllerTest {

    @Test
    @TransactionalReactiveTest
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void getUserPreferences() {
        webClient
                .get()
                .uri("/api/user/{id}/preferences", user.getAdminUser().getId())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @TransactionalReactiveTest
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void updateUserPreferences() {
        final TranslationLocaleDto frenchLocale = locale.createLocale(frLocaleCreationDto()).get();

        try {
            webClient
                    .put()
                    .uri("/api/user/{id}/preferences", user.getAdminUser().getId())
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
                    .uri("/api/user/{id}/preferences", user.getAdminUser().getId())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.toolLocale").isEqualTo(ToolLocale.FRENCH.name())
                    .jsonPath("$.preferredLocales[0]").isEqualTo(frenchLocale.getId());
        } finally {
            user.resetUserPreferences(UserManager.ADMIN_USER_NAME);
        }
    }

    @Test
    @TransactionalReactiveTest
    @WithMockUser(username = UserManager.ADMIN_USER_NAME, roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void updateUserPreferencesMissingLocale() {
        try {
            webClient
                    .put()
                    .uri("/api/user/{id}/preferences", user.getAdminUser().getId())
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
            user.resetUserPreferences(UserManager.ADMIN_USER_NAME);
        }
    }

}
