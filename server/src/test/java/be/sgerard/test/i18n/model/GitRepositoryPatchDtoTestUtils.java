package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryPatchDto;
import be.sgerard.i18n.model.repository.dto.GitRepositoryPatchDto;

import static be.sgerard.test.i18n.model.RepositoryEntityTestUtils.*;

/**
 * @author Sebastien Gerard
 */
public final class GitRepositoryPatchDtoTestUtils {

    private GitRepositoryPatchDtoTestUtils() {
    }

    public static GitHubRepositoryPatchDto.Builder i18nToolGitHubRepositoryPatchDto() {
        return GitHubRepositoryPatchDto.gitHubBuilder()
                .id(I18N_TOOL_GITHUB_ID)
                .defaultBranch(I18N_TOOL_GITHUB_DEFAULT_BRANCH)
                .webHookSecret(I18N_TOOL_GITHUB_WEB_HOOK_SECRET)
                .accessKey(I18N_TOOL_GITHUB_ACCESS_TOKEN);
    }

    public static GitRepositoryPatchDto.Builder i18nToolGitRepositoryPatchDto() {
        return GitRepositoryPatchDto.gitBuilder()
                .id(I18N_TOOL_GIT_ID)
                .defaultBranch(I18N_TOOL_GIT_DEFAULT_BRANCH)
                .username(I18N_TOOL_GIT_REPO_USER)
                .password(I18N_TOOL_GIT_REPO_USER_PASSWORD);
    }
}
