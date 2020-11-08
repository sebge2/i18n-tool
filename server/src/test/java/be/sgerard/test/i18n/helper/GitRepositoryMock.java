package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.service.repository.git.DefaultGitRepositoryApi;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository mocking a remote Git repository.
 * <p>
 * The repository will be initialized based on the content of the {@link Builder#originalGitProject original Git project directory} and
 * will mock the specified {@link Builder#remoteUri URI}.
 *
 * @author Sebastien Gerard
 */
@SuppressWarnings("UnusedReturnValue")
public class GitRepositoryMock {

    public static Builder builder() {
        return new Builder();
    }

    private final URI mockedRemoteUri;
    private final File originalGitProject;
    private final File location;
    private final boolean allowAnonymousRead;
    private final Set<User> users;
    private final Set<UserKey> userKeys;

    private GitRepositoryMock(Builder builder) throws Exception {
        mockedRemoteUri = builder.remoteUri;
        originalGitProject = builder.originalGitProject;
        allowAnonymousRead = builder.allowAnonymousRead;
        users = builder.users;
        userKeys = builder.userKeys;
        location = Files.createTempDirectory("mocked-git-repo").toFile();
    }

    public URI getMockedRemoteUri() {
        return mockedRemoteUri;
    }

    public URI getLocationUri() {
        return new File(location, ".git").toURI();
    }

    public GitRepositoryMock init() throws Exception {
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
    }

    public boolean authenticate(String username, String password) {
        if ((username == null) && (password == null)) {
            return allowAnonymousRead;
        }

        if (password != null) {
            return users.stream().anyMatch(user -> Objects.equals(username, user.getUsername()) && Objects.equals(password, user.getPassword()));
        }

        return userKeys.stream().anyMatch(user -> Objects.equals(username, user.getKey()));
    }

    public GitRepositoryMock createBranches(String... branches) {
        try {
            try (GitRepositoryApi api = createApi()) {
                for (String branch : branches) {
                    api.createBranch(branch);
                }

                return this;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create branch on mock repository.", e);
        }
    }

    public GitRepositoryMock deleteBranches(String... branches) {
        try {
            try (GitRepositoryApi api = createApi()) {
                for (String branch : branches) {
                    api.removeBranch(branch);
                }

                return this;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot delete branch on mock repository.", e);
        }
    }

    public GitRepositoryMock destroy() {
        try {
            try (GitRepositoryApi api = createApi()) {
                api.delete();
            }

            FileUtils.deleteDirectory(location);

            return this;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot delete mock repository.", e);
        }
    }

    public GitRepositoryMock assertFileContent(String branch, File file, String expectedString) {
        try (GitRepositoryApi api = createApi()) {
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

    public GitRepositoryMock mergeTo(String currentBranch, String targetBranch) {
        try (GitRepositoryApi api = createApi()) {
            api
                    .checkout(targetBranch)
                    .merge(currentBranch);
        }

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GitRepositoryMock that = (GitRepositoryMock) o;

        return Objects.equals(mockedRemoteUri, that.mockedRemoteUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mockedRemoteUri);
    }

    private GitRepositoryApi createApi() {
        return DefaultGitRepositoryApi.createAPI(new GitRepositoryApi.Configuration(location));
    }

    public static final class User {

        private final String username;
        private final String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final User user = (User) o;

            return Objects.equals(username, user.username);
        }

        @Override
        public int hashCode() {
            return Objects.hash(username);
        }
    }

    public static final class UserKey {

        private final String key;

        public UserKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public static final class Builder {

        private URI remoteUri;
        private File originalGitProject;
        private boolean allowAnonymousRead;
        private final Set<User> users = new HashSet<>();
        private final Set<UserKey> userKeys = new HashSet<>();

        private Builder() {
        }

        public Builder remoteUri(URI remoteUri) {
            this.remoteUri = remoteUri;
            return this;
        }

        public Builder originalDirectory(File originalGitProject) {
            this.originalGitProject = originalGitProject;
            return this;
        }

        public Builder allowAnonymousRead(boolean allowAnonymousRead) {
            this.allowAnonymousRead = allowAnonymousRead;
            return this;
        }

        public Builder users(Collection<User> users) {
            this.users.addAll(users);
            return this;
        }

        public Builder users(User... users) {
            return users(asList(users));
        }

        public Builder userKeys(Collection<UserKey> userKeys) {
            this.userKeys.addAll(userKeys);
            return this;
        }

        public Builder userKeys(UserKey... userKeys) {
            return userKeys(asList(userKeys));
        }

        public GitRepositoryMock build() throws Exception {
            return new GitRepositoryMock(this);
        }
    }
}
