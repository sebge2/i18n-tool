package be.sgerard.i18n.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Application properties mapped as this POJO.
 *
 * @author Sebastien Gerard
 */
@ConfigurationProperties(prefix = "be.sgerard.i18n")
public class AppProperties {

    private String baseDirectory;
    private Repository repository;
    private Security security;
    private Lock lock;

    public AppProperties() {
        this.repository = new Repository(this);
        this.security = new Security();
    }

    /**
     * Returns the base directory where this service can store files.
     */
    public File getBaseDirectory() {
        return new File(baseDirectory);
    }

    /**
     * Sets the base directory where this service can store files.
     */
    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Returns properties related to {@link Repository repositories}.
     */
    @ConfigurationProperties(prefix = "repository")
    public Repository getRepository() {
        return repository;
    }

    /**
     * Sets properties related to {@link Repository repositories}.
     */
    public AppProperties setRepository(Repository repository) {
        this.repository = repository;
        return this;
    }

    /**
     * Returns properties related to {@link Security security}.
     */
    @ConfigurationProperties(prefix = "security")
    public Security getSecurity() {
        return security;
    }

    /**
     * Sets properties related to {@link Security security}.
     */
    public AppProperties setSecurity(Security security) {
        this.security = security;
        return this;
    }

    /**
     * Returns properties about the locking service.
     */
    @ConfigurationProperties(prefix = "lock")
    public Lock getLock() {
        return lock;
    }

    /**
     * Sets properties about the locking service.
     */
    public AppProperties setLock(Lock lock) {
        this.lock = lock;
        return this;
    }

    /**
     * Properties related to security.
     */
    public static final class Security {

        private String defaultAdminPassword;

        public Security() {
        }

        /**
         * Returns the password to use for the administrator use. Only for the test environment.
         */
        public Optional<String> getDefaultAdminPassword() {
            return Optional.ofNullable(defaultAdminPassword);
        }

        /**
         * Sets the password to use for the administrator use. Only for the test environment.
         */
        public void setDefaultAdminPassword(String defaultAdminPassword) {
            this.defaultAdminPassword = !isEmpty(defaultAdminPassword) ? defaultAdminPassword : null;
        }
    }

    /**
     * Properties related to directories.
     */
    public static final class Repository {

        private final AppProperties appProperties;

        private String directory = "repository";
        private final BundleConfiguration javaProperties = new BundleConfiguration();
        private final BundleConfiguration jsonIcu = new BundleConfiguration();

        public Repository(AppProperties appProperties) {
            this.appProperties = appProperties;
        }

        /**
         * Returns the sub-directory in {@link AppProperties#getBaseDirectory() the base directory} containing repository data.
         */
        public String getDirectory() {
            return directory;
        }

        /**
         * Sets the sub-directory in {@link AppProperties#getBaseDirectory() the base directory} containing repository data.
         */
        public Repository setDirectory(String directory) {
            this.directory = directory;
            return this;
        }

        /**
         * Returns the directory containing repository data.
         */
        public File getDirectoryBaseDir(String id) {
            return new File(new File(appProperties.getBaseDirectory(), getDirectory()), id);
        }

        @ConfigurationProperties(prefix = "java-properties")
        public BundleConfiguration getJavaProperties() {
            return javaProperties;
        }

        @ConfigurationProperties(prefix = "json-icu")
        public BundleConfiguration getJsonIcu() {
            return jsonIcu;
        }
    }

    /**
     * Default configuration for translations bundles.
     */
    public static class BundleConfiguration {

        private List<String> ignoredPaths = new ArrayList<>();
        private List<String> includedPaths = new ArrayList<>();

        public BundleConfiguration() {
        }

        /**
         * Returns all the paths that are included when scanning bundles of this type.
         * By default all paths are included. If paths have been specified only those paths will be scanned,
         * expected if they have been {@link #getIgnoredPaths() ignored}.
         */
        public List<String> getIncludedPaths() {
            return includedPaths;
        }

        /**
         * Sets all the paths that are included when scanning bundles of this type.
         */
        public BundleConfiguration setIncludedPaths(List<String> includedPaths) {
            this.includedPaths = includedPaths;
            return this;
        }

        /**
         * Returns all the paths that are ignored when scanning bundles of this type.
         * <p>
         * Note that ignored paths have a bigger priority over {@link #getIncludedPaths() included paths}.
         */
        public List<String> getIgnoredPaths() {
            return ignoredPaths;
        }

        /**
         * Sets all the paths that are ignored when scanning bundles of this type.
         */
        public BundleConfiguration setIgnoredPaths(List<String> ignoredPaths) {
            this.ignoredPaths = ignoredPaths;
            return this;
        }
    }

    /**
     * Properties related to the locking service.
     */
    public static class Lock {

        private int timeoutInS = 120;

        public Lock() {
        }

        /**
         * Returns the maximum time to get the lock (in seconds) after a timeout exception will be thrown.
         */
        public int getTimeoutInS() {
            return timeoutInS;
        }

        /**
         * Sets the maximum time to get the lock (in seconds) after a timeout exception will be thrown.
         */
        public void setTimeoutInS(int timeoutInS) {
            this.timeoutInS = timeoutInS;
        }
    }
}
