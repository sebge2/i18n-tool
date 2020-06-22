package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.i18n.dto.BundleKeyTranslationDto;
import be.sgerard.test.i18n.support.WithInternalUser;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolRepositoryCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.enLocaleCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frLocaleCreationDto;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TranslationControllerTest extends AbstractControllerTest {

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

    @BeforeEach
    public void setupLocales() throws Exception {
        locale
                .createLocale(frLocaleCreationDto()).and()
                .createLocale(enLocaleCreationDto());

        repository
                .create(i18nToolRepositoryCreationDto())
                .hint("my-repo")
                .initialize()
                .workspaces()
                .sync()
                .workspaceForBranch("master")
                .initialize();
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findTranslation() throws Exception {
        final BundleKeyTranslationDto translation = translations
                .forRepositoryHint("my-repo")
                .forWorkspaceName("master")
                .findOrDie("validation.repository.name-not-unique", Locale.ENGLISH);

        asyncMvc
                .perform(get("/api/translation/{id}", translation.getId()))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(translation.getId()))
                .andExpect(jsonPath("$.locale").value("en"))
                .andExpect(jsonPath("$.originalValue").value("Another repository is already named [{0}]. Names must be unique."))
                .andExpect(jsonPath("$.updatedValue").isEmpty())
                .andExpect(jsonPath("$.lastEditor").isEmpty());
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findTranslationUnknown() throws Exception {
        asyncMvc
                .perform(get("/api/translation/{id}", "unknown-id"))
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void writeTranslations() throws Exception {
        final BundleKeyTranslationDto translation = translations
                .forRepositoryHint("my-repo")
                .forWorkspaceName("master")
                .findOrDie("validation.repository.name-not-unique", Locale.ENGLISH);

        asyncMvc
                .perform(
                        patch("/api/translation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(singletonMap(translation.getId(), "my value updated")))
                )
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(translation.getId()))
                .andExpect(jsonPath("$[0].locale").value("en"))
                .andExpect(jsonPath("$[0].originalValue").value("Another repository is already named [{0}]. Names must be unique."))
                .andExpect(jsonPath("$[0].updatedValue").value("my value updated"))
                .andExpect(jsonPath("$[0].lastEditor").value("fake-user-id"));
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void writeTranslationsUnknownId() throws Exception {
        asyncMvc
                .perform(
                        patch("/api/translation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(singletonMap("an-unknown-id", "my value updated")))
                )
                .andExpectStarted()
                .andWaitResult()
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findTranslations() throws Exception {
        // TODO
    }

}
