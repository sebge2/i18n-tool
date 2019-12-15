package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.GitRepositoryCreationDto;

import static be.sgerard.test.i18n.support.TestUtils.currentProjectLocation;

/**
 * @author Sebastien Gerard
 */
public final class GitRepositoryCreationDtoTestUtils {

    private GitRepositoryCreationDtoTestUtils() {
    }

    public static GitHubRepositoryCreationDto i18nToolRepositoryCreationDto() {
        return new GitHubRepositoryCreationDto("sebge2", "i18n-tool", null);
    }

    public static GitHubRepositoryCreationDto privateI18nToolRepositoryCreationDto() {
        return new GitHubRepositoryCreationDto("sebge2", "private-i18n-tool", "ABCD");
    }

    public static GitRepositoryCreationDto i18nToolLocalRepositoryCreationDto() {
        return new GitRepositoryCreationDto(currentProjectLocation().toString(), "i18n-tool");
    }

}
