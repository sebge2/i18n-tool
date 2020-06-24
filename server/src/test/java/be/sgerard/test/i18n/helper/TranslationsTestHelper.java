package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.i18n.dto.BundleKeyTranslationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsPageDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsSearchRequestDto;
import be.sgerard.i18n.model.repository.dto.RepositoryDto;
import be.sgerard.i18n.model.workspace.WorkspaceDto;
import be.sgerard.test.i18n.support.JsonHolderResultHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.AssertionFailedError;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Sebastien Gerard
 */
@Component
public class TranslationsTestHelper {

    private final AsyncMockMvcTestHelper mockMvc;
    private final ObjectMapper objectMapper;
    private final TranslationLocaleTestHelper localeTestHelper;
    private final RepositoryTestHelper repositoryTestHelper;
    private final WorkspaceTestHelper workspaceTestHelper;

    public TranslationsTestHelper(AsyncMockMvcTestHelper mockMvc,
                                  ObjectMapper objectMapper,
                                  TranslationLocaleTestHelper localeTestHelper,
                                  RepositoryTestHelper repositoryTestHelper,
                                  WorkspaceTestHelper workspaceTestHelper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
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

        public StepWorkspace forWorkspaceName(String branchName) throws Exception {
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

        public TranslationsPageDto get() throws Exception {
            loadTranslations(workspace);

            return translations;
        }

        public Optional<BundleKeyTranslationDto> find(String key, Locale locale) throws Exception {
            loadTranslations(workspace);

            final TranslationLocaleDto translationLocaleDto = localeTestHelper.findRegisteredLocale(locale);

            return  translations.getWorkspaces().stream()
                    .flatMap(workspace -> workspace.getFiles().stream())
                    .flatMap(file -> file.getKeys().stream())
                    .filter(keyDto -> Objects.equals(keyDto.getKey(), key))
                    .flatMap(keyDto -> keyDto.getTranslations().stream())
                    .filter(translation -> Objects.equals(translation.getLocale(), translationLocaleDto.getId()))
                    .findFirst();
        }

        public BundleKeyTranslationDto findOrDie(String key, Locale locale) throws Exception {
            return find(key, locale)
                    .orElseThrow(() -> new AssertionFailedError("There is no translation with key [" + key + "] in locale [" + locale + "]."));
        }

        public StepWorkspace expectTranslation(String key, Locale locale, String expected) throws Exception {
            loadTranslations(workspace);

            final Optional<String> actual = find(key, locale)
                    .map(translation -> Optional.ofNullable(translation.getUpdatedValue()).orElse(translation.getOriginalValue()));

            assertThat(actual).contains(expected);

            return this;
        }

        public StepWorkspace expectNoModification() throws Exception {
            loadTranslations(workspace);

            final List<String> updatedTranslations = translations.getWorkspaces().stream()
                    .flatMap(workspace -> workspace.getFiles().stream())
                    .flatMap(file -> file.getKeys().stream())
                    .flatMap(bundle -> bundle.getTranslations().stream())
                    .map(BundleKeyTranslationDto::getUpdatedValue)
                    .filter(Objects::nonNull)
                    .collect(toList());

            assertThat(updatedTranslations).isEmpty();

            return this;
        }

        private void loadTranslations(WorkspaceDto workspace) throws Exception {
            if (translations != null) {
                return;
            }

            final JsonHolderResultHandler<TranslationsPageDto> handler = new JsonHolderResultHandler<>(objectMapper, TranslationsPageDto.class);

            mockMvc
                    .perform(
                            post("/api/translation/do?action=search")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(
                                            TranslationsSearchRequestDto.builder()
                                                    .workspaces(workspace.getId())
                                                    .build()
                                    ))
                    )
                    .andExpectStarted()
                    .andWaitResult()
                    .andExpect(status().is(OK.value()))
                    .andDo(handler);

            translations = handler.getValue();
        }
    }
}
