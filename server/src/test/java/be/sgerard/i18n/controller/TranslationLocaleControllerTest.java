package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleCreationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frBeWallonLocaleCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frLocaleCreationDto;
import static org.assertj.core.api.Assertions.assertThat;
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
        asyncMvc
                .perform(request(HttpMethod.GET, "/api/translation/locale/"))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @WithMockUser(username = "user-01", roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findAllTranslations() throws Exception {
        locale.createLocale(frBeWallonLocaleCreationDto());

        asyncMvc
                .perform(request(HttpMethod.GET, "/api/translation/locale/"))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.language=='fr')]").exists())
                .andExpect(jsonPath("$[?(@.icon=='flag-icon-fr')]").exists())
                .andExpect(jsonPath("$[?(@.region=='BE')]").exists());
    }

    @Test
    @Transactional
    @WithMockUser(username = "user-01", roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void create() throws Exception {
        final TranslationLocaleCreationDto translationLocale = frBeWallonLocaleCreationDto().build();

        asyncMvc
                .perform(
                        request(HttpMethod.POST, "/api/translation/locale/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(translationLocale))
                )
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.language").value("fr"))
                .andExpect(jsonPath("$.icon").value("flag-icon-fr"))
                .andExpect(jsonPath("$.region").value("BE"))
                .andExpect(jsonPath("$.variants[0]").value("wallon"));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user-01", roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void createTwice() throws Exception {
        locale.createLocale(frBeWallonLocaleCreationDto().build());

        asyncMvc
                .perform(
                        request(HttpMethod.POST, "/api/translation/locale/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(frBeWallonLocaleCreationDto().build()))
                )
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("There is already an existing locale fr-BE [wallon]."));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user-01", roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void update() throws Exception {
        final TranslationLocaleDto originalLocale = locale.createLocale(TranslationLocaleCreationDto.builder().language("fr").icon("flag-icon-fr")).get();

        final TranslationLocaleDto updatedLocale = TranslationLocaleDto.builder()
                .id(originalLocale.getId())
                .language("fr")
                .region("BE")
                .variants("wallon")
                .icon("flag-icon-fr")
                .build();

        asyncMvc
                .perform(
                        request(HttpMethod.PUT, "/api/translation/locale/{id}", originalLocale.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedLocale))
                )
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.language").value("fr"))
                .andExpect(jsonPath("$.icon").value("flag-icon-fr"))
                .andExpect(jsonPath("$.region").value("BE"))
                .andExpect(jsonPath("$.variants[0]").value("wallon"));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user-01", roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void updateConflict() throws Exception {
        final TranslationLocaleDto otherLocale = locale.createLocale(frBeWallonLocaleCreationDto()).get();

        final TranslationLocaleDto currentLocale = locale.createLocale(frLocaleCreationDto()).get();
        final TranslationLocaleDto updatedCurrentLocale = TranslationLocaleDto.builder()
                .id(currentLocale.getId())
                .language(otherLocale.getLanguage())
                .region(otherLocale.getRegion().orElse(null))
                .variants(otherLocale.getVariants())
                .build();

        asyncMvc
                .perform(
                        request(HttpMethod.PUT, "/api/translation/locale/{id}", currentLocale.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedCurrentLocale))
                )
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @WithMockUser(username = "user-01", roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void delete() throws Exception {
        final TranslationLocaleDto translationLocale = locale.createLocale(frBeWallonLocaleCreationDto()).get();

        final int numberLocalesBefore = locale.getLocales().size();

        asyncMvc
                .perform(request(HttpMethod.DELETE, "/api/translation/locale/" + translationLocale.getId()))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isNoContent());

        final int numberLocalesAfter = locale.getLocales().size();

        assertThat(numberLocalesAfter).isEqualTo(numberLocalesBefore - 1);
    }

}
