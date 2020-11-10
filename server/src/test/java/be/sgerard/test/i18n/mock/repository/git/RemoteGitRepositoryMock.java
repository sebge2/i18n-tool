package be.sgerard.test.i18n.mock.repository.git;

import be.sgerard.i18n.service.repository.git.DefaultGitRepositoryApi;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.File;
import java.net.URI;

import static be.sgerard.i18n.support.FileUtils.createTempDirectory;

/**
 * Repository mocking a remote Git repository.
 * <p>
 * The repository will be initialized based on the content of the {@link RemoteGitRepositoryMock#originalGitProject original Git project directory}
 *
 * @author Sebastien Gerard
 */
@SuppressWarnings("UnusedReturnValue")
public class RemoteGitRepositoryMock {

    private final File originalGitProject;
    private final File location;

    public RemoteGitRepositoryMock(File originalGitProject) {
        this.originalGitProject = originalGitProject;
        this.location = createTempDirectory("mocked-git-repo");
    }

    /**
     * Returns the URI to use to target this fake remote repository.
     */
    public URI getLocationUri() {
        return new File(location, ".git").toURI();
    }

    /**
     * Initializes the fake repository, all resources are initialized.
     */
    public RemoteGitRepositoryMock init() {
        try {
            Git.init().setDirectory(location).call();

            final Repository originalRepository = Git.open(originalGitProject).getRepository();

            final Git api = Git.open(location);

            try (ObjectReader reader = originalRepository.newObjectReader(); RevWalk walk = new RevWalk(reader); TreeWalk treeWalk = new TreeWalk(originalRepository, reader)) {
                final ObjectId id = originalRepository.resolve(Constants.HEAD);
                final RevCommit commit = walk.parseCommit(id);
                final RevTree tree = commit.getTree();

                treeWalk.addTree(tree);
                treeWalk.setRecursive(true);

                while (treeWalk.next()) {
                    final File srcFile = new File(originalGitProject, treeWalk.getPathString());
                    final File destFile = new File(location, treeWalk.getPathString());

                    if (srcFile.exists()) {
                        FileUtils.copyFile(srcFile, destFile);

                        api.add().addFilepattern(treeWalk.getPathString()).call();
                    }
                }
            }

            api.commit().setMessage("initial commit").call();

            return this;
        } catch (Exception e) {
            throw new RuntimeException("Error while initializing the repository mock.", e);
        }
    }

    /**
     * Destroys the fake remote repository. All resources are dropped.
     */
    public RemoteGitRepositoryMock destroy() {
        try {
            try (GitRepositoryApi api = openApi()) {
                api.delete();
            }

            FileUtils.deleteDirectory(location);

            return this;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot delete mock repository.", e);
        }
    }

    /**
     * Creates the {@link GitRepositoryApi API} for accessing the mocked repository.
     */
    public GitRepositoryApi openApi() {
        return DefaultGitRepositoryApi.createAPI(new GitRepositoryApi.Configuration(location, URI.create("http://acme.com/my-repository.git")));
    }
}
