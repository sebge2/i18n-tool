package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import com.google.common.io.Files;
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
import java.io.IOException;
import java.net.URI;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * Repository mocking a remote Git repository.
 * <p>
 * The repository will be initialized based on the content of the {@link Builder#originalDirectory original directory} and
 * will mock the specified {@link Builder#remoteUri URI}.
 * <p>
 * The repository location is placed in a temporary folder and it's organized like this:
 * <ul>
 * <li>/master</li>
 * <li>/branchX</li>
 * </ul>
 * <p>
 * By default the repository will have only the master branch based on the content of the {@link Builder#originalDirectory original directory}.
 *
 * @author Sebastien Gerard
 */
public class GitRepositoryMock {

    private final File temporaryDirectory;

    public static Builder builder() {
        return new Builder();
    }

    private final URI remoteUri;
    private final File originalDirectory;
    private final boolean allowAnonymousRead;
    private final Set<User> users;
    private final Set<UserKey> userKeys;

    private GitRepositoryMock(Builder builder) {
        remoteUri = builder.remoteUri;
        originalDirectory = builder.originalDirectory;
        allowAnonymousRead = builder.allowAnonymousRead;
        users = builder.users;
        userKeys = builder.userKeys;
        temporaryDirectory = Files.createTempDir();
    }

    public URI getRemoteUri() {
        return remoteUri;
    }

    public void init() throws Exception {
        final Repository repository = Git.open(originalDirectory).getRepository();

        try (ObjectReader reader = repository.newObjectReader(); RevWalk walk = new RevWalk(reader); TreeWalk treeWalk = new TreeWalk(repository, reader);) {
            final ObjectId id = repository.resolve(Constants.HEAD);
            final RevCommit commit = walk.parseCommit(id);
            final RevTree tree = commit.getTree();

            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);

            while (treeWalk.next()) {
                FileUtils.copyFile(new File(originalDirectory, treeWalk.getPathString()), new File(getBranchDir(GitRepositoryApi.DEFAULT_BRANCH), treeWalk.getPathString()));
            }
        }
    }

    public void destroy() {
        try {
            FileUtils.deleteDirectory(temporaryDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot delete mock repository.", e);
        }
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

    public List<String> listBranches() {
        return Optional.ofNullable(temporaryDirectory.list())
                .map(Arrays::asList)
                .orElse(emptyList())
                .stream()
                .filter(file -> new File(temporaryDirectory, file).isDirectory())
                .collect(toList());
    }

    public void copyTo(String branch, File targetDirectory) throws Exception {
        if (targetDirectory.exists()) {
            FileUtils.cleanDirectory(targetDirectory);
        }

        FileUtils.copyDirectory(getBranchDir(branch), targetDirectory);
    }

    public void copyFrom(String branch, File originalDirectory) throws Exception {
        if (getBranchDir(branch).exists()) {
            FileUtils.cleanDirectory(getBranchDir(branch));
        }

        FileUtils.copyDirectory(originalDirectory, getBranchDir(branch));
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

        return Objects.equals(remoteUri, that.remoteUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(remoteUri);
    }

    private File getBranchDir(String branch) {
        return new File(temporaryDirectory, branch);
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
        private File originalDirectory;
        private boolean allowAnonymousRead;
        private final Set<User> users = new HashSet<>();
        private final Set<UserKey> userKeys = new HashSet<>();

        private Builder() {
        }

        public Builder remoteUri(URI remoteUri) {
            this.remoteUri = remoteUri;
            return this;
        }

        public Builder originalDirectory(File originalDirectory) {
            this.originalDirectory = originalDirectory;
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

        public GitRepositoryMock build() {
            return new GitRepositoryMock(this);
        }
    }
}
