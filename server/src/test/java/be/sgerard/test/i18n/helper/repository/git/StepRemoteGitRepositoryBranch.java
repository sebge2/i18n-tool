package be.sgerard.test.i18n.helper.repository.git;

import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.test.i18n.mock.repository.git.RemoteGitRepositoryMock;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Manages all branches of a remote repository.
 *
 * @author Sebastien Gerard
 */
@SuppressWarnings("UnusedReturnValue")
public class StepRemoteGitRepositoryBranch {

    private final RemoteGitRepositoryMock remoteGitRepositoryMock;

    public StepRemoteGitRepositoryBranch(RemoteGitRepositoryMock remoteGitRepositoryMock) {
        this.remoteGitRepositoryMock = remoteGitRepositoryMock;
    }

    /**
     * Creates the specified branches on the remote repository.
     */
    public StepRemoteGitRepositoryBranch createBranches(String... branches) {
        try {
            try (GitRepositoryApi api = openApi()) {
                for (String branch : branches) {
                    api.createBranch(branch);
                }

                return this;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create branch on mock repository.", e);
        }
    }

    /**
     * Deletes the specified branches from the remote repository.
     */
    public StepRemoteGitRepositoryBranch deleteBranches(String... branches) {
        try {
            try (GitRepositoryApi api = openApi()) {
                for (String branch : branches) {
                    api.removeBranch(branch);
                }

                return this;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot delete branch on mock repository.", e);
        }
    }

    /**
     * Actions to be performed on the specified branch.
     */
    public StepBranch branch(String branch) {
        return new StepBranch(branch);
    }

    /**
     * Creates the {@link GitRepositoryApi API} for accessing the mocked repository.
     */
    private GitRepositoryApi openApi() {
        return remoteGitRepositoryMock.openApi();
    }

    /**
     * All actions to be performed on a specific branch.
     */
    public final class StepBranch {

        private final String branch;

        public StepBranch(String branch) {
            this.branch = branch;
        }

        /**
         * Makes assertions on the specified file.
         */
        public StepBranchFile file(File file) {
            return new StepBranchFile(branch, file);
        }

        /**
         * Makes assertions on the specified file.
         */
        public StepBranchFile file(String file) {
            return file(new File(file));
        }
    }

    /**
     * All actions to be performed on a specific branch file.
     */
    public final class StepBranchFile {

        private final String branch;
        private final File file;

        public StepBranchFile(String branch, File file) {
            this.branch = branch;
            this.file = file;
        }

        /**
         * Returns assertions for the current branch.
         */
        public StepBranch and() {
            return new StepBranch(branch);
        }

        /**
         * Asserts the content in the current file from the remote repository.
         */
        public StepBranchFile assertContains(String expectedString) {
            try (GitRepositoryApi api = openApi()) {
                api.resetHardHead(); // TODO why files are un-staged?

                try {
                    api.checkout(branch);

                    assertThat(IOUtils.toString(api.openInputStream(file))).contains(expectedString);
                } catch (IOException e) {
                    throw new IllegalStateException("Error while reading file.", e);
                }
            }

            return this;
        }

        /**
         * Writes the content in the current file from the remote repository.
         */
        public StepBranchFile writeContent(String content) {
            try (GitRepositoryApi api = openApi()) {
                api.resetHardHead(); // TODO why files are un-staged?

                try {
                    api.checkout(branch);

                    IOUtils.write(content, api.openOutputStream(file, true));

                    api.commitAll("update file in test");
                } catch (IOException e) {
                    throw new IllegalStateException("Error while writing file.", e);
                }
            }

            return this;
        }

        /**
         * Removes the specified file from the remote repository.
         */
        public StepBranchFile remove() {
            try (GitRepositoryApi api = openApi()) {

                api
                        .checkout(branch)
                        .removeFile(file)
                        .commitAll("remove file in test");
            }

            return this;
        }
    }
}
