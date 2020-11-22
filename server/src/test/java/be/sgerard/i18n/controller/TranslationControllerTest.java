package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.i18n.dto.*;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.auth.internal.WithJaneDoeAdminUser;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Locale;

import static be.sgerard.test.i18n.model.RepositoryEntityTestUtils.I18N_TOOL_GITHUB_ACCESS_TOKEN;
import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolGitHubRepositoryCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.enLocaleCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frLocaleCreationDto;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasSize;

/**
 * @author Sebastien Gerard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TranslationControllerTest extends AbstractControllerTest {

    @BeforeAll
    public void setupRepo() {
        remoteRepository
                .gitHub()
                .create(i18nToolGitHubRepositoryCreationDto(), "myGitHubRepo")
                .accessToken(I18N_TOOL_GITHUB_ACCESS_TOKEN)
                .onCurrentGitProject()
                .start();
    }

    @AfterAll
    public void destroy() {
        remoteRepository.stopAll();
    }

    @BeforeEach
    public void setupLocales() {
        locale
                .createLocale(frLocaleCreationDto()).and()
                .createLocale(enLocaleCreationDto());

        repository
                .create(i18nToolGitHubRepositoryCreationDto())
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
    public void searchTranslationsEmptyValueIsMissing() {
        final String key = "other-value.empty-value";
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
                .jsonPath("$.rows[0].translations[0].originalValue").isEmpty()
                .jsonPath("$.rows[0].translations[1].originalValue").isEmpty();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void searchTranslationsFromTranslationValueOriginalValue() {
        final String key = "validation.repository.name-not-unique";
        webClient
                .post()
                .uri("/api/translation/do?action=search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        TranslationsSearchRequestDto.builder()
                                .valueRestriction(new TranslationValueRestrictionDto("Another repository is already named", TranslationValueRestrictionDto.ValuePatternStrategy.CONTAINS, null))
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
    public void searchTranslationsFromTranslationValueUpdatedValue() {
        final String key = "validation.repository.name-not-unique";

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
                                .valueRestriction(new TranslationValueRestrictionDto("my value updated", TranslationValueRestrictionDto.ValuePatternStrategy.CONTAINS, null))
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.rows").value(hasSize(1))
                .jsonPath("$.rows[0].bundleKey").isEqualTo(key)
                .jsonPath("$.rows[0].translations").value(hasSize(2))
                .jsonPath("$.rows[0].translations[?(@.updatedValue=='my value updated')]").exists();
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
    public void searchTranslationsPaginationNextPage() {
        final TranslationsPageDto workspaceTranslations = translations
                .forRepositoryHint("my-repo")
                .forWorkspaceName("master").translations().get();

        final List<String> locales = this.locale.getSortedLocales().stream().map(TranslationLocaleDto::getId).collect(toList());

        final TranslationsPageRowDto firstRow = workspaceTranslations.getRows().get(0);
        final TranslationsPageRowDto secondRow = workspaceTranslations.getRows().get(1);
        final TranslationsPageRowDto thirdRow = workspaceTranslations.getRows().get(2);
        final String expectedFirstPageFistKey = firstRow.getWorkspace() + firstRow.getBundleFile() + firstRow.getBundleKey();
        final String expectedFirstPageLastKey = secondRow.getWorkspace() + secondRow.getBundleFile() + secondRow.getBundleKey();

        webClient
                .post()
                .uri("/api/translation/do?action=search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        TranslationsSearchRequestDto.builder()
                                .workspace(firstRow.getWorkspace())
                                .locales(locales)
                                .maxKeys(2)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstPageKey").isEqualTo(expectedFirstPageFistKey)
                .jsonPath("$.lastPageKey").isEqualTo(expectedFirstPageLastKey)
                .jsonPath("$.rows").value(hasSize(2))
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
                                .pageSpec(
                                        TranslationsSearchPageSpecDto.builder()
                                                .nextPage(true)
                                                .keyOtherPage(expectedFirstPageLastKey)
                                                .build()
                                )
                                .maxKeys(1)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.lastPageKey").isNotEmpty()
                .jsonPath("$.rows").value(hasSize(1))
                .jsonPath("$.locales").isEqualTo(locales)
                .jsonPath("$.rows[0].bundleKey").isEqualTo(thirdRow.getBundleKey())
                .jsonPath("$.rows[0].workspace").isEqualTo(thirdRow.getWorkspace())
                .jsonPath("$.rows[0].bundleFile").isEqualTo(thirdRow.getBundleFile())
                .jsonPath("$.rows[0].translations").value(hasSize(2));
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void searchTranslationsPaginationPreviousPage() {
        final TranslationsPageDto workspaceTranslations = translations
                .forRepositoryHint("my-repo")
                .forWorkspaceName("master").translations().get();

        final List<String> locales = this.locale.getSortedLocales().stream().map(TranslationLocaleDto::getId).collect(toList());

        final TranslationsPageRowDto firstRow = workspaceTranslations.getRows().get(0);
        final TranslationsPageRowDto secondRow = workspaceTranslations.getRows().get(1);
        final TranslationsPageRowDto thirdRow = workspaceTranslations.getRows().get(2);
        final String expectedFirstPageFistKey = firstRow.getWorkspace() + firstRow.getBundleFile() + firstRow.getBundleKey();
        final String expectedFirstPageLastKey = secondRow.getWorkspace() + secondRow.getBundleFile() + secondRow.getBundleKey();
        final String expectedSecondPageLastKey = thirdRow.getWorkspace() + thirdRow.getBundleFile() + thirdRow.getBundleKey();

        webClient
                .post()
                .uri("/api/translation/do?action=search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        TranslationsSearchRequestDto.builder()
                                .locales(locales)
                                .workspace(firstRow.getWorkspace())
                                .pageSpec(
                                        TranslationsSearchPageSpecDto.builder()
                                                .nextPage(false)
                                                .keyOtherPage(expectedSecondPageLastKey)
                                                .build()
                                )
                                .maxKeys(2)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstPageKey").isEqualTo(expectedFirstPageFistKey)
                .jsonPath("$.lastPageKey").isEqualTo(expectedFirstPageLastKey)
                .jsonPath("$.rows").value(hasSize(2))
                .jsonPath("$.locales").isEqualTo(locales)
                .jsonPath("$.rows[0].bundleKey").isEqualTo(firstRow.getBundleKey())
                .jsonPath("$.rows[0].workspace").isEqualTo(firstRow.getWorkspace())
                .jsonPath("$.rows[0].bundleFile").isEqualTo(firstRow.getBundleFile())
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
                .findBundleKeyOrDie("validation.repository.name-not-unique")
                .get()
                .getId();

        final String localeId = locale.findRegisteredLocale(Locale.ENGLISH).get().getId();

        webClient
                .put()
                .uri("/api/translation/bundle-key/{bundleKeyId}/locale/{localeId}", bundleKeyId, localeId)
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("my value updated")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.locale").isEqualTo(localeId)
                .jsonPath("$.originalValue").isEqualTo("Another repository is already named [{0}]. Names must be unique.")
                .jsonPath("$.updatedValue").isEqualTo("my value updated")
                .jsonPath("$.lastEditor").isNotEmpty();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void writeTranslationUnknownId() {
        webClient
                .put()
                .uri("/api/translation/bundle-key/{bundleKeyId}/locale/{localeId}", "unknown-id", "another-unknown-id")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("my value updated")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void writeTranslations() {
        final String bundleKeyId = translations
                .forRepositoryHint("my-repo")
                .forWorkspaceName("master")
                .translations()
                .findBundleKeyOrDie("validation.repository.name-not-unique")
                .get()
                .getId();

        final String englishLocaleId = locale.findRegisteredLocale(Locale.ENGLISH).get().getId();
        final String frenchLocaleId = locale.findRegisteredLocale(Locale.FRENCH).get().getId();

        webClient
                .put()
                .uri("/api/translation/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(asList(
                        TranslationUpdateDto.builder().bundleKeyId(bundleKeyId).localeId(englishLocaleId).translation("my value updated").build(),
                        TranslationUpdateDto.builder().bundleKeyId(bundleKeyId).localeId(frenchLocaleId).translation("ma valeur").build()
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].locale").isEqualTo(englishLocaleId)
                .jsonPath("$[0].originalValue").isEqualTo("Another repository is already named [{0}]. Names must be unique.")
                .jsonPath("$[0].updatedValue").isEqualTo("my value updated")
                .jsonPath("$[0].lastEditor").isNotEmpty()
                .jsonPath("$[1].locale").isEqualTo(frenchLocaleId)
                .jsonPath("$[1].originalValue").isEqualTo("Il existe déjà un répository nommé [{0]. Les noms doivent être unique.")
                .jsonPath("$[1].updatedValue").isEqualTo("ma valeur")
                .jsonPath("$[1].lastEditor").isNotEmpty();
    }
}
