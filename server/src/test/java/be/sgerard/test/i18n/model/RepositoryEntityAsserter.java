package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.i18n.persistence.BundleConfigurationEntity;
import be.sgerard.i18n.model.repository.persistence.BaseGitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
public class RepositoryEntityAsserter {

    public static RepositoryEntityAsserter newAssertion() {
        return new RepositoryEntityAsserter();
    }

    public RepositoryEntityAsserter expectEquals(RepositoryEntity actual, RepositoryEntity expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getStatus()).isEqualTo(expected.getStatus());

        assertThat(actual.getTranslationsConfiguration().getIgnoredKeys()).isEqualTo(expected.getTranslationsConfiguration().getIgnoredKeys());
        assertThat(actual.getTranslationsConfiguration().getBundles().stream().map(BundleConfigurationEntity::getBundleType).collect(toSet()))
                .isEqualTo(expected.getTranslationsConfiguration().getBundles().stream().map(BundleConfigurationEntity::getBundleType).collect(toSet()));
        actual.getTranslationsConfiguration().getBundles().stream()
                .map(BundleConfigurationEntity::getBundleType)
                .forEach(bundleType -> {
                    assertThat(actual.getTranslationsConfiguration().getBundleOrDie(bundleType).getIgnoredPaths())
                            .isEqualTo(expected.getTranslationsConfiguration().getBundleOrDie(bundleType).getIgnoredPaths());
                    assertThat(actual.getTranslationsConfiguration().getBundleOrDie(bundleType).getIncludedPaths())
                            .isEqualTo(expected.getTranslationsConfiguration().getBundleOrDie(bundleType).getIncludedPaths());
                });

        assertThat(actual.getClass()).isEqualTo(expected.getClass());

        if (actual instanceof GitRepositoryEntity) {
            doExpectEquals((GitRepositoryEntity) actual, (GitRepositoryEntity) expected);
        } else if (actual instanceof GitHubRepositoryEntity) {
            doExpectEquals((GitHubRepositoryEntity) actual, (GitHubRepositoryEntity) expected);
        } else {
            throw new UnsupportedOperationException("Unsupported user [" + actual + "].");
        }

        return this;
    }

    private void doExpectEquals(BaseGitRepositoryEntity actual, BaseGitRepositoryEntity expected) {
        assertThat(actual.getLocation()).isEqualTo(expected.getLocation());
        assertThat(actual.getDefaultBranch()).isEqualTo(expected.getDefaultBranch());
        assertThat(actual.getAllowedBranches().toString()).isEqualTo(expected.getAllowedBranches().toString());
    }

    private void doExpectEquals(GitRepositoryEntity actual, GitRepositoryEntity expected) {
        doExpectEquals(actual, (BaseGitRepositoryEntity) expected);

        assertThat(actual.getUsername()).isEqualTo(expected.getUsername());
        assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
    }

    private void doExpectEquals(GitHubRepositoryEntity actual, GitHubRepositoryEntity expected) {
        doExpectEquals(actual, (BaseGitRepositoryEntity) expected);

        assertThat(actual.getUsername()).isEqualTo(expected.getUsername());
        assertThat(actual.getRepository()).isEqualTo(expected.getRepository());
        assertThat(actual.getAccessKey()).isEqualTo(expected.getAccessKey());
        assertThat(actual.getWebHookSecret()).isEqualTo(expected.getWebHookSecret());
    }
}
