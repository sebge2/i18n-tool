package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.i18n.dto.BundleKeyTranslationDto;
import be.sgerard.test.i18n.support.TransactionalReactiveTest;
import be.sgerard.test.i18n.support.WithInternalUser;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Locale;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolRepositoryCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.enLocaleCreationDto;
import static be.sgerard.test.i18n.model.TranslationLocaleCreationDtoTestUtils.frLocaleCreationDto;
import static java.util.Collections.singletonMap;
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
                .sync()
                .workspaceForBranch("master")
                .initialize();
    }

    @Test
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void findTranslation() {
        final TranslationsPageTranslationDto translation = translations
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
                .jsonPath("$.locale").isEqualTo(locale.findRegisteredLocale(Locale.ENGLISH).getId())
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
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
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
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
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
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
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
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
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
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void searchTranslationsByLocale() {
        webClient
                .post()
                .uri("/api/translation/do?action=search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        TranslationsSearchRequestDto.builder()
                                .locales(locale.getLocales().get(0).getId())
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.rows[?(@.translations size 1)]").exists();
    }

    @Test
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
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
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void searchTranslationsPagination() {
        final TranslationsPageDto workspaceTranslations = translations
                .forRepositoryHint("my-repo")
                .forWorkspaceName("master")
                .get();

        final List<String> locales = this.locale.getSortedLocales().stream().map(TranslationLocaleDto::getId).collect(toList());

        final TranslationsPageRowDto firstRow = workspaceTranslations.getRows().get(0);
        final TranslationsPageRowDto secondRow = workspaceTranslations.getRows().get(1);

        webClient
                .post()
                .uri("/api/translation/do?action=search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(
                        TranslationsSearchRequestDto.builder()
                                .workspaces(firstRow.getWorkspace())
                                .locales(locales)
                                .maxKeys(1)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pageIndex").isEqualTo(0)
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
                                .workspaces(firstRow.getWorkspace())
                                .pageIndex(1)
                                .maxKeys(1)
                                .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pageIndex").isEqualTo(1)
                .jsonPath("$.rows").value(hasSize(1))
                .jsonPath("$.locales").isEqualTo(locales)
                .jsonPath("$.rows[0].bundleKey").isEqualTo(secondRow.getBundleKey())
                .jsonPath("$.rows[0].workspace").isEqualTo(secondRow.getWorkspace())
                .jsonPath("$.rows[0].bundleFile").isEqualTo(secondRow.getBundleFile())
                .jsonPath("$.rows[0].translations").value(hasSize(2));
    }

    @Test
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void searchTranslationsUpdated() {
        final TranslationsPageTranslationDto translation = translations
                .forRepositoryHint("my-repo")
                .forWorkspaceName("master")
                .findOrDie("validation.repository.name-not-unique", Locale.ENGLISH);

        webClient
                .patch()
                .uri("/api/translation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(singletonMap(translation.getId(), "my value updated"))
                .exchange()
                .expectStatus().isOk();

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
    @TransactionalReactiveTest
    @WithInternalUser(roles = {"MEMBER_OF_ORGANIZATION", "ADMIN"})
    public void writeTranslations() {
        final TranslationsPageTranslationDto translation = translations
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
