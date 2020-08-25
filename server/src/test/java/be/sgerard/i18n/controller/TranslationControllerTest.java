package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.i18n.dto.*;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.WithJaneDoeAdminUser;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Locale;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolRepositoryCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.enLocaleCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frLocaleCreationDto;
import static java.util.stream.Collectors.toList;
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
                .workspaceForBranch("master")
                .initialize();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void searchTranslationsFromJavaProperties() {
        final String key = "validation.repository.name-not-unique";
        webClient
                .post()
                .uri("/api/translation/do?action=search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        TranslationsSearchRequestDto.builder()
                                .keyPattern(new TranslationKeyPatternDto(TranslationKeyPatternDto.KeyPatternStrategy.EQUAL, key))
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.rows").value(hasSize(1))
                .jsonPath("$.rows[0].bundleKey").isEqualTo(key)
                .jsonPath("$.rows[0].translations").value(hasSize(2))
                .jsonPath("$.rows[0].translations[?(@.originalValue=='Another repository is already named [{0}]. Names must be unique.')]").exists();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void searchTranslationsFromJson() {
        final String key = "SHARED.REPOSITORY_TITLE";
        webClient
                .post()
                .uri("/api/translation/do?action=search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        TranslationsSearchRequestDto.builder()
                                .keyPattern(new TranslationKeyPatternDto(TranslationKeyPatternDto.KeyPatternStrategy.EQUAL, key))
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.rows").value(hasSize(1))
                .jsonPath("$.rows[0].bundleKey").isEqualTo(key)
                .jsonPath("$.rows[0].translations").value(hasSize(2))
                .jsonPath("$.rows[0].translations[?(@.originalValue=='Répertoire')]").exists();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void searchTranslationsByKeyEqual() {
        final String key = "validation.repository.name-not-unique";
        webClient
                .post()
                .uri("/api/translation/do?action=search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        TranslationsSearchRequestDto.builder()
                                .keyPattern(new TranslationKeyPatternDto(TranslationKeyPatternDto.KeyPatternStrategy.EQUAL, key))
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.rows").value(hasSize(1))
                .jsonPath("$.rows[0].bundleKey").isEqualTo(key)
                .jsonPath("$.rows[0].translations").value(hasSize(2))
                .jsonPath("$.rows[0].translations[?(@.originalValue=='Another repository is already named [{0}]. Names must be unique.')]").exists();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void searchTranslationsByKeyStartWith() {
        final String key = "validation.repository.name-not-unique";

        webClient
                .post()
                .uri("/api/translation/do?action=search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        TranslationsSearchRequestDto.builder()
                                .keyPattern(new TranslationKeyPatternDto(TranslationKeyPatternDto.KeyPatternStrategy.STARTS_WITH, key.substring(0, key.length() - 10)))
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.rows").value(hasSize(1))
                .jsonPath("$.rows[0].bundleKey").isEqualTo(key)
                .jsonPath("$.rows[0].translations").value(hasSize(2))
                .jsonPath("$.rows[0].translations[?(@.originalValue=='Another repository is already named [{0}]. Names must be unique.')]").exists();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void searchTranslationsByKeyEndWith() {
        final String key = "validation.repository.name-not-unique";

        webClient
                .post()
                .uri("/api/translation/do?action=search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        TranslationsSearchRequestDto.builder()
                                .keyPattern(new TranslationKeyPatternDto(TranslationKeyPatternDto.KeyPatternStrategy.ENDS_WITH, key.substring(10)))
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.rows").value(hasSize(1))
                .jsonPath("$.rows[0].bundleKey").isEqualTo(key)
                .jsonPath("$.rows[0].translations").value(hasSize(2))
                .jsonPath("$.rows[0].translations[?(@.originalValue=='Another repository is already named [{0}]. Names must be unique.')]").exists();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void searchTranslationsByLocale() {
        webClient
                .post()
                .uri("/api/translation/do?action=search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        TranslationsSearchRequestDto.builder()
                                .locale(locale.getLocales().get(0).getId())
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.rows[?(@.translations size 1)]").exists();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void searchTranslationsMax1Key() {
        webClient
                .post()
                .uri("/api/translation/do?action=search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        TranslationsSearchRequestDto.builder()
                                .maxKeys(1)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.rows").value(hasSize(1));
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void searchTranslationsPagination() {
        final TranslationsPageDto workspaceTranslations = translations
                .forRepositoryHint("my-repo")
                .forWorkspaceName("master").translations().get();

        final List<String> locales = this.locale.getSortedLocales().stream().map(TranslationLocaleDto::getId).collect(toList());

        final TranslationsPageRowDto firstRow = workspaceTranslations.getRows().get(0);
        final TranslationsPageRowDto secondRow = workspaceTranslations.getRows().get(1);
        final String expectedLastFistKey = firstRow.getWorkspace() + firstRow.getBundleFile() + firstRow.getBundleKey();

        webClient
                .post()
                .uri("/api/translation/do?action=search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        TranslationsSearchRequestDto.builder()
                                .workspace(firstRow.getWorkspace())
                                .locales(locales)
                                .maxKeys(1)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.lastPageKey").isEqualTo(expectedLastFistKey)
                .jsonPath("$.rows").value(hasSize(1))
                .jsonPath("$.locales").isEqualTo(locales)
                .jsonPath("$.rows[0].bundleKey").isEqualTo(firstRow.getBundleKey())
                .jsonPath("$.rows[0].workspace").isEqualTo(firstRow.getWorkspace())
                .jsonPath("$.rows[0].bundleFile").isEqualTo(firstRow.getBundleFile())
                .jsonPath("$.rows[0].translations").value(hasSize(2));

        webClient
                .post()
                .uri("/api/translation/do?action=search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        TranslationsSearchRequestDto.builder()
                                .locales(locales)
                                .workspace(firstRow.getWorkspace())
                                .lastPageKey(expectedLastFistKey)
                                .maxKeys(1)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.lastPageKey").isNotEmpty()
                .jsonPath("$.rows").value(hasSize(1))
                .jsonPath("$.locales").isEqualTo(locales)
                .jsonPath("$.rows[0].bundleKey").isEqualTo(secondRow.getBundleKey())
                .jsonPath("$.rows[0].workspace").isEqualTo(secondRow.getWorkspace())
                .jsonPath("$.rows[0].bundleFile").isEqualTo(secondRow.getBundleFile())
                .jsonPath("$.rows[0].translations").value(hasSize(2));
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void searchTranslationsUpdated() {
        translations
                .forRepositoryHint("my-repo")
                .forWorkspaceName("master")
                .updateTranslation("validation.repository.name-not-unique", Locale.ENGLISH, "my value updated");

        webClient
                .post()
                .uri("/api/translation/do?action=search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        TranslationsSearchRequestDto.builder()
                                .criterion(TranslationSearchCriterion.UPDATED_TRANSLATIONS)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.rows").value(hasSize(1))
                .jsonPath("$.rows[0].bundleKey").isEqualTo("validation.repository.name-not-unique");
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void writeTranslation() {
        final String bundleKeyId = translations
                .forRepositoryHint("my-repo")
                .forWorkspaceName("master")
                .translations()
                .findBunglePageRowOrDie("validation.repository.name-not-unique")
                .getId();

        final String localeId = locale.findRegisteredLocale(Locale.ENGLISH).getId();

        webClient
                .patch()
                .uri("/api/translation/bundle-key/{bundleKeyId}/locale/{localeId}", bundleKeyId, localeId)
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("my value updated")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.locale").isEqualTo(locale.findRegisteredLocale(Locale.ENGLISH).getId())
                .jsonPath("$.originalValue").isEqualTo("Another repository is already named [{0}]. Names must be unique.")
                .jsonPath("$.updatedValue").isEqualTo("my value updated")
                .jsonPath("$.lastEditor").isNotEmpty();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void writeTranslationUnknownId() {
        webClient
                .patch()
                .uri("/api/translation/bundle-key/{bundleKeyId}/locale/{localeId}", "unknown-id", "another-unknown-id")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("my value updated")
                .exchange()
                .expectStatus().isNotFound();
    }
}
