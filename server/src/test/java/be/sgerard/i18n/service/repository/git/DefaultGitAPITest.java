package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.repository.RepositoryException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
public class DefaultGitAPITest {

    public static final String REPO_LOCATION = "https://github.com/sebge2/i18n-tool.git";

    private static DefaultGitAPI.Configuration configuration;
    private static GitAPI api;

    @BeforeClass
    public static void setup() throws IOException {
        configuration = new DefaultGitAPI.Configuration(URI.create(REPO_LOCATION), generateTemporaryFile());

        api = DefaultGitAPI.createAPI(configuration);
        api.init();
    }

    @AfterClass
    public static void tearDown() throws IOException {
        FileUtils.deleteDirectory(configuration.getRepositoryLocation());
    }

    @Test
    public void validateInfo() throws Exception {
        DefaultGitAPI.createAPI(new DefaultGitAPI.Configuration(URI.create(REPO_LOCATION), generateTemporaryFile()))
                .validateInfo();
    }

    @Test(expected = ValidationException.class)
    public void validateInfoWrongUrl() throws Exception {
        DefaultGitAPI
                .createAPI(new DefaultGitAPI.Configuration(URI.create("https://github.com/sebge2/unknown.git"), generateTemporaryFile()))
                .validateInfo();
    }

    @Test(expected = ValidationException.class)
    public void validateInfoWrongCredentials() throws Exception {
        DefaultGitAPI
                .createAPI(
                        new DefaultGitAPI.Configuration(URI.create("https://github.com/sebge2/unknown.git"), generateTemporaryFile())
                                .setUsername("sebge2")
                                .setPassword("password")
                )
                .validateInfo();
    }

    @Test
    public void update() {
        final GitAPI update = api.update();

        assertThat(update).isSameAs(api);
    }

    @Test
    public void getCurrentBranch() {
        final String actual = api.getCurrentBranch();
        assertThat(actual).isEqualTo(DefaultGitAPI.DEFAULT_BRANCH);
    }

    @Test
    public void listLocalBranches() {
        final List<String> actual = api.listLocalBranches();

        assertThat(actual).contains(DefaultGitAPI.DEFAULT_BRANCH);
    }

    @Test
    public void listRemoteBranches() {
        final List<String> actual = api.listRemoteBranches();

        assertThat(actual).contains(DefaultGitAPI.DEFAULT_BRANCH);
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

    @Test(expected = RepositoryException.class)
    public void checkoutUnknown() {
        api.checkout("unknown branch");
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

    @Test(expected = RepositoryException.class)
    public void removeDefaultBranch() {
        api.removeBranch(configuration.getDefaultBranch());
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

    @Test(expected = RepositoryException.class)
    public void createBranchTwice() {
        final String branch = "DefaultGitAPITest";

        try {
            api.createBranch(branch);
            api.createBranch(branch);
        } finally {
            api
                    .checkout(configuration.getDefaultBranch())
                    .removeBranch(branch);
        }
    }

    @Test
    public void listFiles() throws Exception {
        final List<String> actual = api.listAllFiles(new File("/")).map(File::getName).collect(toList());

        assertThat(actual).contains("README.adoc", "LICENSE");
    }

    @Test
    public void listNormalFiles() throws Exception {
        final List<String> actual = api.listNormalFiles(new File("/")).map(File::getName).collect(toList());

        assertThat(actual).doesNotContain("server", "front");
    }

    @Test
    public void listDirectories() throws Exception {
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
        final File actual = api.openAsTemp(new File("LICENSE"));

        assertThat(FileUtils.readFileToString(actual)).contains("Apache License");
    }

    @Test
    public void openOutputStream() throws Exception {
        final File file = new File("LICENSE");
        try {
            try (OutputStream actual = api.openOutputStream(file)) {
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

    public static File generateTemporaryFile() throws IOException {
        return Files.createTempDirectory("test-").toFile();
    }
}
