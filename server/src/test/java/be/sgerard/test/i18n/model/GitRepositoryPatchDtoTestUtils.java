package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryPatchDto;
import be.sgerard.i18n.model.repository.dto.GitRepositoryPatchDto;

/**
 * @author Sebastien Gerard
 */
public final class GitRepositoryPatchDtoTestUtils {

    public static final String I18N_TOOL_REPO_WEB_HOOK_SECRET = "j$E]HLR4wTKbvF_}";
    public static final String I18N_TOOL_REPO_ACCESS_TOKEN = "639dd7cd-862f-4c25-b73a-fb30f66652d6";

    public static final String I18N_TOOL_REPO_USER = "i18n-tool-user";
    public static final String I18N_TOOL_REPO_USER_PASSWORD = "WqbN2RVckuNbb6yxmBDgRxdm";

    private GitRepositoryPatchDtoTestUtils() {
    }

    public static GitHubRepositoryPatchDto.Builder i18nToolGitHubRepositoryPatchDto() {
        return GitHubRepositoryPatchDto.gitHubBuilder()
                .id("76ad5ae4-272d-4706-a97b-10206d22cc3a")
                .defaultBranch("develop")
                .webHookSecret(I18N_TOOL_REPO_WEB_HOOK_SECRET)
                .accessKey(I18N_TOOL_REPO_ACCESS_TOKEN);
    }

    public static GitRepositoryPatchDto.Builder i18nToolGitRepositoryPatchDto() {
        return GitRepositoryPatchDto.gitBuilder()
                .id("d7a53916-9406-4577-b00f-b22cd8bb2690")
                .defaultBranch("develop")
                .username(I18N_TOOL_REPO_USER)
                .password(I18N_TOOL_REPO_USER_PASSWORD);
    }
}
