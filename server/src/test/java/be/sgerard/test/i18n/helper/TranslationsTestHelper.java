package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.i18n.dto.*;
import be.sgerard.i18n.model.repository.dto.RepositoryDto;
import be.sgerard.i18n.model.workspace.dto.WorkspaceDto;
import junit.framework.AssertionFailedError;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
@Component
public class TranslationsTestHelper {

    private final WebTestClient webClient;
    private final TranslationLocaleTestHelper localeTestHelper;
    private final RepositoryTestHelper repositoryTestHelper;
    private final WorkspaceTestHelper workspaceTestHelper;

    public TranslationsTestHelper(WebTestClient webClient, TranslationLocaleTestHelper localeTestHelper,
                                  RepositoryTestHelper repositoryTestHelper,
                                  WorkspaceTestHelper workspaceTestHelper) {
        this.webClient = webClient;
        this.localeTestHelper = localeTestHelper;
        this.repositoryTestHelper = repositoryTestHelper;
        this.workspaceTestHelper = workspaceTestHelper;
    }

    public StepRepository forRepositoryHint(String hint) {
        return new StepRepository(repositoryTestHelper.forHint(hint).get());
    }

    public class StepRepository {

        private final RepositoryDto repository;

        private StepRepository(RepositoryDto repository) {
            this.repository = repository;
        }

        public StepWorkspace forWorkspaceName(String branchName) {
            return new StepWorkspace(workspaceTestHelper.with(repository).workspaceForBranch(branchName).get());
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public class StepWorkspace {

        private final WorkspaceDto workspace;

        private StepWorkspace(WorkspaceDto workspace) {
            this.workspace = workspace;
        }

        public StepWorkspacesTranslationsPage translations() {
            return new StepWorkspacesTranslationsPage(workspace, loadTranslations(workspace));
        }

        public void updateTranslation(String bundleKey, Locale locale, String updatedValue) {
            final String bundleKeyId = translations().findBundlePageRowOrDie(bundleKey).getId();
            final String localeId = localeTestHelper.findRegisteredLocale(locale).getId();

            webClient
                    .put()
                    .uri("/api/translation/bundle-key/{bundleKeyId}/locale/{localeId}", bundleKeyId, localeId)
                    .contentType(MediaType.TEXT_PLAIN)
                    .bodyValue(updatedValue)
                    .exchange()
                    .expectStatus().isOk();
        }

        private TranslationsPageDto loadTranslations(WorkspaceDto workspace) {
            return webClient.post()
                    .uri("/api/translation/do?action=search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(
                            TranslationsSearchRequestDto.builder()
                                    .workspace(workspace.getId())
                                    .maxKeys(500)
                                    .build()
                    ))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(TranslationsPageDto.class)
                    .returnResult()
                    .getResponseBody();
        }
    }

    public class StepWorkspacesTranslationsPage {

        private final WorkspaceDto workspace;
        private final TranslationsPageDto page;

        public StepWorkspacesTranslationsPage(WorkspaceDto workspace, TranslationsPageDto page) {
            this.workspace = workspace;
            this.page = page;
        }

        public TranslationsPageDto get() {
            return this.page;
        }

        public StepWorkspace and() {
            return new StepWorkspace(workspace);
        }

        public Optional<TranslationsPageRowDto> findBunglePageRow(String bundleKey) {
            return page.getRows().stream()
                    .filter(row -> Objects.equals(row.getBundleKey(), bundleKey))
                    .findFirst();
        }

        public TranslationsPageRowDto findBundlePageRowOrDie(String bundleKey) {
            return findBunglePageRow(bundleKey)
                    .orElseThrow(() -> new AssertionFailedError("There is no bundle with key [" + bundleKey + "]."));
        }

        public Optional<TranslationsPageTranslationDto> findTranslation(String key, Locale locale) {
            final TranslationLocaleDto translationLocaleDto = localeTestHelper.findRegisteredLocale(locale);

            return findBunglePageRow(key)
                    .map(row -> row.getTranslations().get(page.getLocales().indexOf(translationLocaleDto.getId())));
        }

        public TranslationsPageTranslationDto findTranslationOrDie(String key, Locale locale) {
            return findTranslation(key, locale)
                    .orElseThrow(() -> new AssertionFailedError("There is no translation with key [" + key + "] in locale [" + locale + "]."));
        }

        public StepWorkspacesTranslationsPage expectTranslation(String key, Locale locale, String expected) {
            final Optional<String> actual = findTranslation(key, locale)
                    .flatMap(translation -> translation.getUpdatedValue().or(translation::getOriginalValue));

            assertThat(actual).contains(expected);

            return this;
        }

        public StepWorkspacesTranslationsPage expectNoModification() {
            final List<String> updatedTranslations = page.getRows().stream()
                    .flatMap(row -> row.getTranslations().stream())
                    .map(TranslationsPageTranslationDto::getUpdatedValue)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(toList());

            assertThat(updatedTranslations).isEmpty();

            return this;
        }
    }
}
