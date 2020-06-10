package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleCreationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.test.i18n.support.JsonHolderResultHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frBeWallonLocaleCreationDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
public class TranslationLocaleControllerTest extends AbstractControllerTest {

    @Test
    @Transactional
    @WithMockUser(username = "user-01", roles = {"MEMBER_OF_ORGANIZATION"})
    public void findAllTranslationsNonAdminAllowed() throws Exception {
        mvc.perform(request(HttpMethod.GET, "/api/translation/locale/"))
                .andExpect(status().is(OK.value()));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user-01", roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findAllTranslations() throws Exception {
        final TranslationLocaleDto translationLocale = locale.createLocale(frBeWallonLocaleCreationDto().build());

        mvc.perform(request(HttpMethod.GET, "/api/translation/locale/"))
                .andExpect(status().is(OK.value()))
                .andExpect(jsonPath("$[?(@.language=='" + translationLocale.getLanguage() + "')]").exists())
                .andExpect(jsonPath("$[?(@.icon=='" + translationLocale.getIcon() + "')]").exists())
                .andExpect(jsonPath("$[?(@.region=='" + translationLocale.getRegion() + "')]").exists());
    }

    @Test
    @Transactional
    @WithMockUser(username = "user-01", roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void create() throws Exception {
        final TranslationLocaleCreationDto translationLocale = frBeWallonLocaleCreationDto().build();

        final int numberLocalesBefore = locale.getLocales().size();
        final JsonHolderResultHandler<TranslationLocaleDto> handler = new JsonHolderResultHandler<>(objectMapper, TranslationLocaleDto.class);

        mvc
                .perform(
                        request(HttpMethod.POST, "/api/translation/locale/")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(objectMapper.writeValueAsString(translationLocale))
                )
                .andExpect(status().is(CREATED.value()))
                .andDo(handler)
                .andExpect(jsonPath("$.language").value(translationLocale.getLanguage()))
                .andExpect(jsonPath("$.icon").value(translationLocale.getIcon()))
                .andExpect(jsonPath("$.region").value(translationLocale.getRegion()))
                .andExpect(jsonPath("$.variants[0]").value(translationLocale.getVariants().get(0)));

        final int numberLocalesAfter = locale.getLocales().size();

        assertThat(numberLocalesAfter).isEqualTo(numberLocalesBefore + 1);

        final TranslationLocaleDto actual = locale.getLocaleByIdOrDie(handler.getValue().getId());

        assertThat(actual.getLanguage()).isEqualTo(translationLocale.getLanguage());
        assertThat(actual.getIcon()).isEqualTo(translationLocale.getIcon());
        assertThat(actual.getRegion()).isEqualTo(translationLocale.getRegion());
        assertThat(actual.getVariants()).isEqualTo(translationLocale.getVariants());
    }

    // TODO update

    @Test
    @Transactional
    @WithMockUser(username = "user-01", roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void delete() throws Exception {
        final TranslationLocaleDto translationLocale = locale.createLocale(frBeWallonLocaleCreationDto().build());

        final int numberLocalesBefore = locale.getLocales().size();

        mvc
                .perform(request(HttpMethod.DELETE, "/api/translation/locale/" + translationLocale.getId()))
                .andExpect(status().is(NO_CONTENT.value()));

        final int numberLocalesAfter = locale.getLocales().size();

        assertThat(numberLocalesAfter).isEqualTo(numberLocalesBefore - 1);
    }

}
