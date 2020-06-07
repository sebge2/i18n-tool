package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.GitRepositoryCreationDto;

import java.io.File;

/**
 * @author Sebastien Gerard
 */
public final class GitRepositoryCreationDtoTestUtils {

    private GitRepositoryCreationDtoTestUtils() {
    }

    public static GitHubRepositoryCreationDto i18nToolRepositoryCreationDto() {
        return new GitHubRepositoryCreationDto("sebge2", "i18n-tool", null);
    }


    public static GitRepositoryCreationDto i18nToolLocalRepositoryCreationDto() {
        return new GitRepositoryCreationDto(
                new File(GitRepositoryCreationDtoTestUtils.class.getResource("/application-test.yml").getFile()).getParentFile().getParentFile().getParentFile().getParent(),
                "i18n-tool"
        );
    }

}
