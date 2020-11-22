package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.repository.github.external.BaseGitHubWebHookEventDto;
import be.sgerard.i18n.model.repository.github.external.GitHubBranchCreatedEventDto;

import static be.sgerard.test.i18n.model.RepositoryEntityTestUtils.I18N_TOOL_GITHUB_NAME;

/**
 * @author Sebastien Gerard
 */
public final class GitHubBranchCreatedEventDtoTestUtils {

    private GitHubBranchCreatedEventDtoTestUtils() {
    }

    public static GitHubBranchCreatedEventDto i18nToolRelease20205BranchCreatedEvent() {
        return new GitHubBranchCreatedEventDto(
                new BaseGitHubWebHookEventDto.Repository("048650d2-7a90-4086-9315-2960acf71099", I18N_TOOL_GITHUB_NAME),
                "branch",
                "release/2020.5"
        );
    }
}
