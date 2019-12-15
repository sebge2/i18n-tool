package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryCreationDto;

/**
 * @author Sebastien Gerard
 */
public final class GitRepositoryCreationDtoTestUtils {

    private GitRepositoryCreationDtoTestUtils() {
    }

    public static GitHubRepositoryCreationDto i18nToolRepositoryCreationDto() {
        return new GitHubRepositoryCreationDto("sebge2", "i18n-tool", null);
    }

}
