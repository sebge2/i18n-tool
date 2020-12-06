package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.GitRepositoryCreationDto;

import static be.sgerard.test.i18n.model.RepositoryEntityTestUtils.*;

/**
 * @author Sebastien Gerard
 */
public final class GitRepositoryCreationDtoTestUtils {

    private GitRepositoryCreationDtoTestUtils() {
    }

    public static GitHubRepositoryCreationDto i18nToolGitHubRepositoryCreationDto() {
        return new GitHubRepositoryCreationDto(I18N_TOOL_GITHUB_USERNAME, I18N_TOOL_GITHUB_REPOSITORY, I18N_TOOL_GITHUB_ACCESS_TOKEN);
    }

    public static GitHubRepositoryCreationDto i18nToolGitHubRepositoryCreationNoAccessTokenDto() {
        return new GitHubRepositoryCreationDto(I18N_TOOL_GITHUB_USERNAME, I18N_TOOL_GITHUB_REPOSITORY, null);
    }

    public static GitRepositoryCreationDto i18nToolGitRepositoryCreationDto() {
        return new GitRepositoryCreationDto(I18N_TOOL_GIT_LOCATION, I18N_TOOL_GIT_NAME, I18N_TOOL_GIT_REPO_USER, I18N_TOOL_GIT_REPO_USER_PASSWORD);
    }

    public static GitRepositoryCreationDto i18nToolGitRepositoryCreationNoUserDto() {
        return new GitRepositoryCreationDto(I18N_TOOL_GIT_LOCATION, I18N_TOOL_GIT_NAME, null, null);
    }

}
