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

        public StepWorkspace updateTranslation(String bundleKey, Locale locale, String updatedValue) {
            translations()
                    .findBundleKeyOrDie(bundleKey)
                    .findTranslationOrDie(locale)
                    .updateTranslation(updatedValue);

            return this;
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

        public Optional<StepTranslationsPageRow> findBundleKey(String bundleKey) {
            return page.getRows().stream()
                    .filter(row -> Objects.equals(row.getBundleKey(), bundleKey))
                    .findFirst()
                    .map(row -> new StepTranslationsPageRow(workspace, page, row));
        }

        public StepTranslationsPageRow findBundleKeyOrDie(String bundleKey) {
            return findBundleKey(bundleKey)
                    .orElseThrow(() -> new AssertionFailedError("There is no bundle with key [" + bundleKey + "]."));
        }

        public StepWorkspacesTranslationsPage expectTranslation(String bundleKey, Locale locale, String expected) {
            findBundleKeyOrDie(bundleKey)
                    .findTranslationOrDie(locale)
                    .expectValue(expected);

            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
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

    public class StepTranslationsPageRow {

        private final WorkspaceDto workspace;
        private final TranslationsPageDto page;
        private final TranslationsPageRowDto row;

        public StepTranslationsPageRow(WorkspaceDto workspace, TranslationsPageDto page, TranslationsPageRowDto row) {
            this.workspace = workspace;
            this.page = page;
            this.row = row;
        }

        public TranslationsPageRowDto get() {
            return this.row;
        }

        public StepWorkspacesTranslationsPage and() {
            return new StepWorkspacesTranslationsPage(workspace, page);
        }

        public Optional<StepTranslationsPageTranslation> findTranslation(Locale locale) {
            final TranslationLocaleDto translationLocaleDto = localeTestHelper.findRegisteredLocale(locale);

            return Optional
                    .ofNullable(row.getTranslations().get(page.getLocales().indexOf(translationLocaleDto.getId())))
                    .map(translation -> new StepTranslationsPageTranslation(workspace, page, row, translation, translationLocaleDto));
        }

        public StepTranslationsPageTranslation findTranslationOrDie(Locale locale) {
            return findTranslation(locale)
                    .orElseThrow(() -> new AssertionFailedError("There is no translation for locale [" + locale + "]."));
        }
    }

    public class StepTranslationsPageTranslation {

        private final WorkspaceDto workspace;
        private final TranslationsPageDto page;
        private final TranslationsPageRowDto row;
        private final TranslationsPageTranslationDto translation;
        private final TranslationLocaleDto translationLocale;

        public StepTranslationsPageTranslation(WorkspaceDto workspace,
                                               TranslationsPageDto page,
                                               TranslationsPageRowDto row,
                                               TranslationsPageTranslationDto translation,
                                               TranslationLocaleDto translationLocale) {
            this.workspace = workspace;
            this.page = page;
            this.row = row;
            this.translation = translation;
            this.translationLocale = translationLocale;
        }

        public TranslationsPageTranslationDto get() {
            return this.translation;
        }

        public StepTranslationsPageRow and() {
            return new StepTranslationsPageRow(workspace, page, row);
        }

        @SuppressWarnings("UnusedReturnValue")
        public StepTranslationsPageTranslation expectValue(String expected) {
            assertThat(translation.getUpdatedValue().or(translation::getOriginalValue)).contains(expected);

            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public StepTranslationsPageTranslation updateTranslation(String updatedValue) {
            webClient
                    .put()
                    .uri("/api/translation/bundle-key/{bundleKeyId}/locale/{localeId}", row.getBundleKeyId(), translationLocale.getId())
                    .contentType(MediaType.TEXT_PLAIN)
                    .bodyValue(updatedValue)
                    .exchange()
                    .expectStatus().isOk();

            System.out.println(row.getBundleKeyId() + " " + translationLocale.getId());

            return this;
        }
    }
}
