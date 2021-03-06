package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.test.i18n.support.GitHubCredentialsTest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
@GitHubCredentialsTest
public class DefaultGitRepositoryApiTest {

    public static final String REPO_LOCATION = "https://github.com/sebge2/i18n-tool.git";

    private static DefaultGitRepositoryApi.Configuration configuration;
    private static GitRepositoryApi api;

    @BeforeAll
    public static void setup() throws IOException {
        configuration = new DefaultGitRepositoryApi.Configuration(generateTemporaryFile(), REPO_LOCATION);

        api = DefaultGitRepositoryApi.createAPI(configuration);
        api.init();
    }

    @AfterEach
    public void rollback() {
        api.resetHardHead();
    }

    @AfterAll
    public static void tearDown() throws IOException {
        api.close();

        FileUtils.deleteDirectory(configuration.getRepositoryLocation());
    }

    @Test
    public void validateInfo() throws Exception {
        final File repositoryLocation = generateTemporaryFile();

        try {
            final GitRepositoryApi.Configuration configuration = new DefaultGitRepositoryApi.Configuration(repositoryLocation, REPO_LOCATION);

            try (GitRepositoryApi api = DefaultGitRepositoryApi.createAPI(configuration)) {
                final ValidationResult validationResult = api.validateInfo();

                assertThat(validationResult.isSuccessful()).isTrue();
            }
        } finally {
            FileUtils.deleteDirectory(repositoryLocation);
        }
    }

    @Test
    public void validateInfoWrongUrl() throws IOException {
        final File repositoryLocation = generateTemporaryFile();

        try {
            final GitRepositoryApi.Configuration configuration = new DefaultGitRepositoryApi.Configuration(repositoryLocation, "https://github.com/sebge2/unknown.git");

            try (GitRepositoryApi api = DefaultGitRepositoryApi.createAPI(configuration)) {
                final ValidationResult validationResult = api.validateInfo();

                assertThat(validationResult.isSuccessful()).isFalse();
            }
        } finally {
            FileUtils.deleteDirectory(repositoryLocation);
        }
    }

    @Test
    public void validateInfoWrongCredentials() throws IOException {
        final File repositoryLocation = generateTemporaryFile();

        try {
            final GitRepositoryApi.Configuration configuration = new DefaultGitRepositoryApi.Configuration(repositoryLocation, "https://github.com/sebge2/unknown.git")
                    .setUsername("sebge2")
                    .setPassword("password");

            try (GitRepositoryApi api = DefaultGitRepositoryApi.createAPI(configuration)) {
                final ValidationResult validationResult = api.validateInfo();

                assertThat(validationResult.isSuccessful()).isFalse();
            }
        } finally {
            FileUtils.deleteDirectory(repositoryLocation);
        }
    }

    @Test
    public void pull() {
        final GitRepositoryApi update = api.pull();

        assertThat(update).isSameAs(api);
    }

    @Test
    public void getCurrentBranch() {
        final String actual = api.getCurrentBranch();

        assertThat(actual).isEqualTo(DefaultGitRepositoryApi.DEFAULT_BRANCH);
    }

    @Test
    public void listLocalBranches() {
        final List<String> actual = api.listLocalBranches();

        assertThat(actual).contains(DefaultGitRepositoryApi.DEFAULT_BRANCH);
    }

    @Test
    public void listRemoteBranches() {
        final List<String> actual = api.listRemoteBranches();

        assertThat(actual).contains(DefaultGitRepositoryApi.DEFAULT_BRANCH);
    }

    @Test
    public void checkoutRemote() {
        try {
            api.checkout("develop");

            assertThat(api.getCurrentBranch()).isEqualTo("develop");
        } finally {
            api.checkout(configuration.getDefaultBranch());
        }
    }

    @Test
    public void checkoutUnknown() {
        Assertions.assertThrows(RepositoryException.class, () -> api.checkout("unknown branch"));
    }

    @Test
    public void checkoutLocal() {
        try {
            api.checkout("develop");

            assertThat(api.getCurrentBranch()).isEqualTo("develop");

            api.checkout(configuration.getDefaultBranch());

            assertThat(api.getCurrentBranch()).isEqualTo(configuration.getDefaultBranch());

            api.checkout("develop");

            assertThat(api.getCurrentBranch()).isEqualTo("develop");
        } finally {
            api.checkout(configuration.getDefaultBranch());
        }
    }

    @Test
    public void resetHardHead() {
        try {
            api.resetHardHead();
        } finally {
            api.checkout(configuration.getDefaultBranch());
        }
    }

    @Test
    public void remove() {
        final String branch = "DefaultGitAPITest";

        try {
            api.createBranch(branch);

            api.checkout(configuration.getDefaultBranch());

            api.removeBranch(branch);
        } finally {
            api
                    .checkout(configuration.getDefaultBranch())
                    .removeBranch(branch);
        }
    }

    @Test
    public void removeCurrentBranch() {
        final String branch = "DefaultGitAPITest";

        try {
            api.createBranch(branch).checkout(branch);

            api.removeBranch(branch);

            assertThat(api.getCurrentBranch()).isEqualTo(configuration.getDefaultBranch());
        } finally {
            api
                    .checkout(configuration.getDefaultBranch())
                    .removeBranch(branch);
        }
    }

    @Test
    public void removeDefaultBranch() {
        Assertions.assertThrows(RepositoryException.class, () -> api.removeBranch(configuration.getDefaultBranch()));
    }

    @Test
    public void createBranch() {
        final String branch = "DefaultGitAPITest";

        try {
            api.createBranch(branch);

            assertThat(api.getCurrentBranch()).isEqualTo(branch);
        } finally {
            api
                    .checkout(configuration.getDefaultBranch())
                    .removeBranch(branch);
        }
    }

    @Test
    public void createBranchTwice() {
        final String branch = "DefaultGitAPITest";

        try {
            api.createBranch(branch);
            Assertions.assertThrows(RepositoryException.class, () -> api.createBranch(branch));
        } finally {
            api
                    .checkout(configuration.getDefaultBranch())
                    .removeBranch(branch);
        }
    }

    @Test
    public void listFiles() {
        final List<String> actual = api.listAllFiles(new File("/")).map(File::getName).collect(toList());

        assertThat(actual).contains("README.adoc", "LICENSE");
    }

    @Test
    public void listNormalFiles() {
        final List<String> actual = api.listNormalFiles(new File("/")).map(File::getName).collect(toList());

        assertThat(actual).doesNotContain("server", "front");
    }

    @Test
    public void listDirectories() {
        final List<String> actual = api.listDirectories(new File("/")).map(File::getName).collect(toList());

        assertThat(actual).contains("server", "front");
    }

    @Test
    public void openInputStream() throws Exception {
        try (InputStream actual = api.openInputStream(new File("LICENSE"))) {

            assertThat(IOUtils.toString(actual)).contains("Apache License");
        }
    }

    @Test
    public void openAsTemp() throws Exception {
        final File actual = api.openAsTemp(new File("LICENSE"), true);

        assertThat(FileUtils.readFileToString(actual)).contains("Apache License");
    }

    @Test
    public void openAsTempNotExist() throws Exception {
        final File actual = api.openAsTemp(new File("unknown"), true);

        assertThat(FileUtils.readFileToString(actual)).contains("");
    }

    @Test
    public void openOutputStream() throws Exception {
        final File file = new File("LICENSE");
        try {
            try (OutputStream actual = api.openOutputStream(file, true)) {
                IOUtils.write("this is a test", actual);
            }

            assertThat(IOUtils.toString(api.openInputStream(file))).isEqualTo("this is a test");
        } finally {
            api.revert(file);
        }
    }

    @Test
    public void openOutputStreamNotExist() throws Exception {
        final File file = new File("unknown");
        try {
            try (OutputStream actual = api.openOutputStream(file, true)) {
                IOUtils.write("this is a test", actual);
            }

            assertThat(IOUtils.toString(api.openInputStream(file))).isEqualTo("this is a test");
        } finally {
            api.revert(file);
        }
    }

    @Test
    public void isClosed() {
        final boolean actual = api.isClosed();

        assertThat(actual).isEqualTo(false);
    }

    private static File generateTemporaryFile() throws IOException {
        return Files.createTempDirectory("test-").toFile();
    }
}
