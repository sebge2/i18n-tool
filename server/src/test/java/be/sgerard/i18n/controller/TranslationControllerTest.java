package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.i18n.dto.BundleKeyTranslationDto;
import be.sgerard.test.i18n.support.TransactionalReactiveTest;
import be.sgerard.test.i18n.support.WithInternalUser;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import java.util.Locale;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolRepositoryCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.enLocaleCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frLocaleCreationDto;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.hasSize;

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
    public void setupLocales() {
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
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findTranslation() {
        final BundleKeyTranslationDto translation = translations
                .forRepositoryHint("my-repo")
                .forWorkspaceName("master")
                .findOrDie("validation.repository.name-not-unique", Locale.ENGLISH);

        webClient
                .get()
                .uri("/api/translation/{id}", translation.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(translation.getId())
                .jsonPath("$.locale").isEqualTo(translation.getLocale())
                .jsonPath("$.originalValue").isEqualTo("Another repository is already named [{0}]. Names must be unique.")
                .jsonPath("$.updatedValue").isEmpty()
                .jsonPath("$.lastEditor").isEmpty();
    }

    @Test
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findTranslationUnknown() {
        webClient
                .get()
                .uri("/api/translation/{id}", "unknown-id")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void writeTranslations() {
        final BundleKeyTranslationDto translation = translations
                .forRepositoryHint("my-repo")
                .forWorkspaceName("master")
                .findOrDie("validation.repository.name-not-unique", Locale.ENGLISH);

        webClient
                .patch()
                .uri("/api/translation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(singletonMap(translation.getId(), "my value updated"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").value(hasSize(1))
                .jsonPath("$[0].id").isEqualTo(translation.getId())
                .jsonPath("$[0].locale").isEqualTo("en")
                .jsonPath("$[0].originalValue").isEqualTo("Another repository is already named [{0}]. Names must be unique.")
                .jsonPath("$[0].updatedValue").isEqualTo("my value updated")
                .jsonPath("$[0].lastEditor").isEqualTo("fake-user-id");
    }

    @Test
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void writeTranslationsUnknownId() {
        webClient
                .patch()
                .uri("/api/translation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(singletonMap("an-unknown-id", "my value updated"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findTranslations() {
        // TODO
    }

}
