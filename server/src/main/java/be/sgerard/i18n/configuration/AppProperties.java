package be.sgerard.i18n.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.util.*;

import static java.util.stream.Collectors.toSet;

/**
 * Application properties mapped as this POJO.
 *
 * @author Sebastien Gerard
 */
@ConfigurationProperties(prefix = "be.sgerard.i18n")
@Getter
@Setter
@Accessors(chain = true)
public class AppProperties {

    /**
     * The base directory where this service can store files.
     */
    private String baseDirectory;

    /**
     * Properties related to {@link Repository repositories}.
     */
    private Repository repository;

    /**
     * Properties related to {@link Security security}.
     */
    private Security security;

    /**
     * Properties about the locking service.
     */
    private Lock lock;

    public AppProperties() {
        this.repository = new Repository(this);
        this.security = new Security();
    }

    /**
     * @see #repository
     */
    @ConfigurationProperties(prefix = "repository")
    public Repository getRepository() {
        return repository;
    }

    /**
     * @see #security
     */
    @ConfigurationProperties(prefix = "security")
    public Security getSecurity() {
        return security;
    }

    /**
     * @see #lock
     */
    @ConfigurationProperties(prefix = "lock")
    public Lock getLock() {
        return lock;
    }

    /**
     * Properties related to security.
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class Security {

        /**
         * The password to use for the administrator use. Only for the test environment.
         */
        private String defaultAdminPassword;

        /**
         * Properties related to {@link GoogleOauth Google OAuth}.
         */
        private GoogleOauth google;

        /**
         * Properties related to {@link GitHubOauthOauth GitHub OAuth}.
         */
        private GitHubOauthOauth github;

        public Security() {
            this.google = new GoogleOauth();
            this.github = new GitHubOauthOauth();
        }

        /**
         * @see #defaultAdminPassword
         */
        public Optional<String> getDefaultAdminPassword() {
            return Optional.ofNullable(defaultAdminPassword);
        }

        /**
         * @see #google
         */
        @ConfigurationProperties(prefix = "google")
        public GoogleOauth getGoogle() {
            return google;
        }

        /**
         * @see #github
         */
        @ConfigurationProperties(prefix = "github")
        public GitHubOauthOauth getGithub() {
            return github;
        }
    }

    /**
     * Properties related to Google OAuth.
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class GoogleOauth {

        /**
         * The email addresses of authenticated user via Google must be one of the specified DNS domain.
         */
        private List<String> restrictedDomains = new ArrayList<>();

        private String toto;

        public GoogleOauth() {
        }

        /**
         * Returns whether the specified email address is authorized.
         */
        public boolean isEmailAuthorized(String email) {
            return restrictedDomains.isEmpty() || restrictedDomains.stream().anyMatch(domain -> email.toLowerCase().endsWith("@" + domain));
        }
    }

    /**
     * Properties related to GitHub OAuth.
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class GitHubOauthOauth {

        /**
         * The email addresses of authenticated user via GitHub must be one of the specified DNS domain.
         */
        private List<String> restrictedDomains = new ArrayList<>();

        /**
         * Users authenticated via GitHub must be part of one of the specified GitHub organization.
         */
        private List<String> restrictedOrganizations = new ArrayList<>();

        public GitHubOauthOauth() {
        }

        /**
         * Returns whether the specified email address is authorized.
         */
        public boolean isEmailAuthorized(String email) {
            return restrictedDomains.isEmpty() || restrictedDomains.stream().anyMatch(domain -> email.toLowerCase().endsWith("@" + domain.toLowerCase()));
        }

        /**
         * Returns whether the specified organization is authorized.
         */
        public boolean isOrganizationAuthorized(Collection<String> organizations) {
            final Set<String> organizationsLowerCases = organizations.stream().map(String::toLowerCase).collect(toSet());
            return restrictedOrganizations.isEmpty()
                    || restrictedOrganizations.stream().map(String::toLowerCase).anyMatch(organizationsLowerCases::contains);
        }
    }

    /**
     * Properties related to directories.
     */
    @Getter
    @Setter
    @Accessors(chain = true)
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

        /**
         * Returns the {@link BundleConfiguration configuration} like to Java properties.
         */
        @ConfigurationProperties(prefix = "java-properties")
        public BundleConfiguration getJavaProperties() {
            return javaProperties;
        }

        /**
         * Returns the {@link BundleConfiguration configuration} like to JSON files.
         */
        @ConfigurationProperties(prefix = "json-icu")
        public BundleConfiguration getJsonIcu() {
            return jsonIcu;
        }
    }

    /**
     * Default configuration for translations bundles.
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class BundleConfiguration {

        /**
         * All the paths that are ignored when scanning bundles of this type.
         * <p>
         * Note that ignored paths have a bigger priority over {@link #getIncludedPaths() included paths}.
         */
        private List<String> ignoredPaths = new ArrayList<>();

        /**
         * All the paths that are included when scanning bundles of this type.
         * By default all paths are included. If paths have been specified only those paths will be scanned,
         * expected if they have been {@link #getIgnoredPaths() ignored}.
         */
        private List<String> includedPaths = new ArrayList<>();

        public BundleConfiguration() {
        }
    }

    /**
     * Properties related to the locking service.
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Lock {

        /**
         * The maximum time to get the lock (in seconds) after a timeout exception will be thrown.
         */
        private int timeoutInS = 120;

        public Lock() {
        }
    }
}
