package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryPatchDto;

/**
 * @author Sebastien Gerard
 */
public final class GitHubRepositoryPatchDtoTestUtils {

    public static final String WEB_HOOK_SECRET = "j$E]HLR4wTKbvF_}";

    private GitHubRepositoryPatchDtoTestUtils() {
    }

    public static GitHubRepositoryPatchDto.Builder i18nToolRepositoryPatchDto() {
        return GitHubRepositoryPatchDto.gitHubBuilder()
                .id("76ad5ae4-272d-4706-a97b-10206d22cc3a")
                .defaultBranch("develop")
                .webHookSecret(WEB_HOOK_SECRET)
                .accessKey("639dd7cd-862f-4c25-b73a-fb30f66652d6");
    }
}
