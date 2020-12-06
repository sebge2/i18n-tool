package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntryEntity;
import be.sgerard.i18n.model.workspace.persistence.GitHubReviewEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;

import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
public class WorkspaceEntityAsserter {

    public static WorkspaceEntityAsserter newAssertion() {
        return new WorkspaceEntityAsserter();
    }

    public WorkspaceEntityAsserter expectEquals(WorkspaceEntity actual, WorkspaceEntity expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getRepository()).isEqualTo(expected.getRepository());
        assertThat(actual.getBranch()).isEqualTo(expected.getBranch());
        assertThat(actual.getStatus()).isEqualTo(expected.getStatus());

        assertThat(actual.getFiles()).hasSameSizeAs(expected.getFiles());

        final Map<String, BundleFileEntity> expectedFiles = toMap(expected);
        for (BundleFileEntity actualFile : actual.getFiles()) {
            assertThat(expectedFiles).containsKey(actualFile.getId());

            doExpectEquals(actualFile, expectedFiles.get(actualFile.getId()));
        }

        assertThat(actual.getLastSynchronization()).isEqualTo(expected.getLastSynchronization());

        if (expected.getReview().isPresent()) {
            assertThat(actual.getReview()).isNotEmpty();
            assertThat(actual.getClass()).isEqualTo(expected.getClass());

            if (expected.getReview().get() instanceof GitHubReviewEntity) {
                doExpectEquals((GitHubReviewEntity) actual.getReview().get(), (GitHubReviewEntity) expected.getReview().get());
            } else {
                throw new UnsupportedOperationException("Unsupported review [" + expected.getReview().get() + "].");
            }
        } else {
            assertThat(actual.getReview()).isEmpty();
        }

        return this;
    }

    private void doExpectEquals(GitHubReviewEntity actual, GitHubReviewEntity expected) {
        assertThat(actual.getPullRequestBranch()).isEqualTo(expected.getPullRequestBranch());
        assertThat(actual.getPullRequestNumber()).isEqualTo(expected.getPullRequestNumber());
    }

    private void doExpectEquals(BundleFileEntity actual, BundleFileEntity expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getLocation()).isEqualTo(expected.getLocation());
        assertThat(actual.getType()).isEqualTo(expected.getType());

        assertThat(actual.getFiles()).hasSameSizeAs(expected.getFiles());

        final Map<String, BundleFileEntryEntity> expectedEntries = toMap(expected);
        for (BundleFileEntryEntity actualEntry : actual.getFiles()) {
            assertThat(expectedEntries).containsKey(actualEntry.getId());

            doExpectEquals(actualEntry, expectedEntries.get(actualEntry.getId()));
        }

        assertThat(actual.getNumberKeys()).isEqualTo(expected.getNumberKeys());
    }

    private void doExpectEquals(BundleFileEntryEntity actual, BundleFileEntryEntity expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getFile()).isEqualTo(expected.getFile());
        assertThat(actual.getLocale()).isEqualTo(expected.getLocale());
    }

    private Map<String, BundleFileEntity> toMap(WorkspaceEntity actual) {
        return actual.getFiles().stream().collect(Collectors.toMap(BundleFileEntity::getId, file -> file));
    }

    private Map<String, BundleFileEntryEntity> toMap(BundleFileEntity actual) {
        return actual.getFiles().stream().collect(Collectors.toMap(BundleFileEntryEntity::getId, file -> file));
    }
}
