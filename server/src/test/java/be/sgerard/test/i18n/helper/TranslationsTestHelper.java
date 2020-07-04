package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.i18n.dto.BundleKeyTranslationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsPageDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsSearchRequestDto;
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

        private TranslationsPageDto translations;

        private StepWorkspace(WorkspaceDto workspace) {
            this.workspace = workspace;
        }

        public TranslationsPageDto get() {
            loadTranslations(workspace);

            return translations;
        }

        public Optional<BundleKeyTranslationDto> find(String key, Locale locale) {
            loadTranslations(workspace);

            final TranslationLocaleDto translationLocaleDto = localeTestHelper.findRegisteredLocale(locale);

            return translations.getRows().stream()
                    .filter(row -> Objects.equals(row.getBundleKey(), key))
                    .flatMap(row -> row.getTranslations().stream())
                    .filter(translation -> Objects.equals(translation.getLocale(), translationLocaleDto.getId()))
                    .findFirst();
        }

        public BundleKeyTranslationDto findOrDie(String key, Locale locale) {
            return find(key, locale)
                    .orElseThrow(() -> new AssertionFailedError("There is no translation with key [" + key + "] in locale [" + locale + "]."));
        }

        public StepWorkspace expectTranslation(String key, Locale locale, String expected) {
            loadTranslations(workspace);

            final Optional<String> actual = find(key, locale)
                    .flatMap(translation -> translation.getUpdatedValue().or(translation::getOriginalValue));

            assertThat(actual).contains(expected);

            return this;
        }

        public StepWorkspace expectNoModification() {
            loadTranslations(workspace);

            final List<String> updatedTranslations = translations.getRows().stream()
                    .flatMap(row -> row.getTranslations().stream())
                    .map(BundleKeyTranslationDto::getUpdatedValue)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(toList());

            assertThat(updatedTranslations).isEmpty();

            return this;
        }

        private void loadTranslations(WorkspaceDto workspace) {
            if (translations != null) {
                return;
            }

            translations = webClient.post()
                    .uri("/api/translation/do?action=search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(
                            TranslationsSearchRequestDto.builder()
                                    .workspaces(workspace.getId())
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
}
