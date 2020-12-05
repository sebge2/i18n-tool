package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.repository.github.external.BaseGitHubWebHookEventDto;
import be.sgerard.i18n.model.repository.github.external.GitHubBranchDeletedEventDto;

import static be.sgerard.test.i18n.model.RepositoryEntityTestUtils.I18N_TOOL_GITHUB_NAME;

/**
 * @author Sebastien Gerard
 */
public final class GitHubBranchDeletedEventDtoTestUtils {

    private GitHubBranchDeletedEventDtoTestUtils() {
    }

    public static GitHubBranchDeletedEventDto i18nToolRelease20204BranchDeletedEvent() {
        return new GitHubBranchDeletedEventDto(
                new BaseGitHubWebHookEventDto.Repository("048650d2-7a90-4086-9315-2960acf71099", I18N_TOOL_GITHUB_NAME),
                "branch",
                "release/2020.4"
        );
    }
}
