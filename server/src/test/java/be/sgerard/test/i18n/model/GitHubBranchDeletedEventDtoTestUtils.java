package be.sgerard.test.i18n.model;

import be.sgerard.i18n.service.github.external.BaseGitHubWebHookEventDto;
import be.sgerard.i18n.service.github.external.GitHubBranchDeletedEventDto;

/**
 * @author Sebastien Gerard
 */
public final class GitHubBranchDeletedEventDtoTestUtils {

    private GitHubBranchDeletedEventDtoTestUtils() {
    }

    public static GitHubBranchDeletedEventDto i18nToolRelease20204BranchDeletedEvent() {
        return new GitHubBranchDeletedEventDto(
                new BaseGitHubWebHookEventDto.Repository("048650d2-7a90-4086-9315-2960acf71099", "sebge2/i18n-tool"),
                "branch",
                "release/2020.4"
        );
    }
}
