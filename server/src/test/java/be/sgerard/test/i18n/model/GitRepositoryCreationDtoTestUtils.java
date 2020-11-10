package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.GitRepositoryCreationDto;

import static be.sgerard.test.i18n.model.GitRepositoryPatchDtoTestUtils.*;
import static be.sgerard.test.i18n.support.TestUtils.currentProjectLocation;

/**
 * @author Sebastien Gerard
 */
public final class GitRepositoryCreationDtoTestUtils {

    private GitRepositoryCreationDtoTestUtils() {
    }

    public static GitHubRepositoryCreationDto i18nToolGitHubRepositoryCreationDto() {
        return new GitHubRepositoryCreationDto("sebge2", "i18n-tool", I18N_TOOL_REPO_ACCESS_TOKEN);
    }

    public static GitHubRepositoryCreationDto i18nToolGitHubRepositoryCreationNoAccessTokenDto() {
        return new GitHubRepositoryCreationDto(i18nToolGitHubRepositoryCreationDto().getUsername(), i18nToolGitHubRepositoryCreationDto().getRepository(), null);
    }

    public static GitRepositoryCreationDto i18nToolGitRepositoryCreationDto() {
        return new GitRepositoryCreationDto(currentProjectLocation().toString(), "i18n-tool", I18N_TOOL_REPO_USER, I18N_TOOL_REPO_USER_PASSWORD);
    }

    public static GitRepositoryCreationDto i18nToolGitRepositoryCreationNoUserDto() {
        return new GitRepositoryCreationDto(i18nToolGitRepositoryCreationDto().getLocation(), i18nToolGitRepositoryCreationDto().getName(), null, null);
    }

}
