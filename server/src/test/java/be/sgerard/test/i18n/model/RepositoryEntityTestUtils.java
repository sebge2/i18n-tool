package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.persistence.BundleConfigurationEntity;
import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.github.GitHubRepositoryId;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;

import java.util.regex.Pattern;

import static be.sgerard.test.i18n.support.TestUtils.currentProjectLocation;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * @author Sebastien Gerard
 */
public final class RepositoryEntityTestUtils {

    public static final String I18N_TOOL_GITHUB_ID = "76ad5ae4-272d-4706-a97b-10206d22cc3a";
    public static final String I18N_TOOL_GITHUB_USERNAME = "sebge2";
    public static final String I18N_TOOL_GITHUB_REPOSITORY = "i18n-tool";
    public static final String I18N_TOOL_GITHUB_NAME = new GitHubRepositoryId(I18N_TOOL_GITHUB_USERNAME, I18N_TOOL_GITHUB_REPOSITORY).toFullName();
    public static final String I18N_TOOL_GITHUB_LOCATION = new GitHubRepositoryId(I18N_TOOL_GITHUB_USERNAME, I18N_TOOL_GITHUB_REPOSITORY).toURI().toString();
    public static final String I18N_TOOL_GITHUB_WEB_HOOK_SECRET = "j$E]HLR4wTKbvF_}";
    public static final String I18N_TOOL_GITHUB_ACCESS_TOKEN = "639dd7cd-862f-4c25-b73a-fb30f66652d6";
    public static final String I18N_TOOL_GITHUB_DEFAULT_BRANCH = "develop";

    public static final String I18N_TOOL_GIT_ID = "d7a53916-9406-4577-b00f-b22cd8bb2690";
    public static final String I18N_TOOL_GIT_NAME = "i18n-tool";
    public static final String I18N_TOOL_GIT_LOCATION = currentProjectLocation().toString();
    public static final String I18N_TOOL_GIT_REPO_USER = "i18n-tool-user";
    public static final String I18N_TOOL_GIT_REPO_USER_PASSWORD = "WqbN2RVckuNbb6yxmBDgRxdm";
    public static final String I18N_TOOL_GIT_DEFAULT_BRANCH = "develop";

    private RepositoryEntityTestUtils() {
    }

    public static GitHubRepositoryEntity i18nToolGitHubRepository() {
        final GitHubRepositoryEntity entity = (GitHubRepositoryEntity) new GitHubRepositoryEntity(I18N_TOOL_GITHUB_USERNAME, I18N_TOOL_GITHUB_REPOSITORY)
                .setAccessKey(I18N_TOOL_GITHUB_ACCESS_TOKEN)
                .setWebHookSecret(I18N_TOOL_GITHUB_WEB_HOOK_SECRET)
                .setDefaultBranch(I18N_TOOL_GITHUB_DEFAULT_BRANCH)
                .setLocation(I18N_TOOL_GITHUB_LOCATION)
                .setAllowedBranches(Pattern.compile("^master|develop|development|release\\/[0-9]{4}.[0-9]{1,2}$"))
                .setName(I18N_TOOL_GITHUB_NAME)
                .setStatus(RepositoryStatus.INITIALIZED)
                .setId(I18N_TOOL_GITHUB_ID);

        entity.getTranslationsConfiguration().addBundles(
                new BundleConfigurationEntity(BundleType.JAVA_PROPERTIES)
                        .setIncludedPaths(singletonList("/server/src/main/resources/"))
                        .setIgnoredPaths(singletonList("/server/src/test/resources/"))
        );

        entity.getTranslationsConfiguration()
                .setIgnoredKeys(asList("first-root.first", "first-root.second", "second-root.sub-level.first", "other-value.empty-value"));

        return entity;
    }

    public static GitRepositoryEntity i18nToolGitRepository() {
        final GitRepositoryEntity entity = (GitRepositoryEntity) new GitRepositoryEntity(I18N_TOOL_GITHUB_USERNAME, I18N_TOOL_GITHUB_REPOSITORY)
                .setUsername(I18N_TOOL_GIT_REPO_USER)
                .setPassword(I18N_TOOL_GIT_REPO_USER_PASSWORD)
                .setDefaultBranch(I18N_TOOL_GIT_DEFAULT_BRANCH)
                .setLocation(I18N_TOOL_GIT_LOCATION)
                .setAllowedBranches(Pattern.compile("^master|develop|development|release\\/[0-9]{4}.[0-9]{1,2}$"))
                .setName(I18N_TOOL_GIT_NAME)
                .setStatus(RepositoryStatus.INITIALIZED)
                .setId(I18N_TOOL_GIT_ID);

        entity.getTranslationsConfiguration().addBundles(
                new BundleConfigurationEntity(BundleType.JAVA_PROPERTIES)
                        .setIncludedPaths(singletonList("/server/src/main/resources/"))
                        .setIgnoredPaths(singletonList("/server/src/test/resources/"))
        );

        entity.getTranslationsConfiguration()
                .setIgnoredKeys(asList("first-root.first", "first-root.second", "second-root.sub-level.first", "other-value.empty-value"));

        return entity;
    }
}
