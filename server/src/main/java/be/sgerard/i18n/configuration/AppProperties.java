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

    /**
     * Properties about scheduled tasks.
     */
    private ScheduledTask scheduledTask;

    public AppProperties() {
        this.repository = new Repository(this);
        this.security = new Security();
        this.scheduledTask = new ScheduledTask();
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
     * @see #scheduledTask
     */
    @ConfigurationProperties(prefix = "scheduled-task")
    public ScheduledTask getScheduledTask() {
        return scheduledTask;
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
         * Returns whether there is a restriction on the GitHub organization.
         */
        public boolean isOrganizationRestricted() {
            return !restrictedOrganizations.isEmpty();
        }

        /**
         * Returns whether the specified organization is authorized.
         */
        public boolean isOrganizationAuthorized(Collection<String> organizations) {
            final Set<String> organizationsLowerCases = organizations.stream().map(String::toLowerCase).collect(toSet());
            return !isOrganizationRestricted()
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

        /**
         * The sub-directory in {@link AppProperties#getBaseDirectory() the base directory} containing repository data.
         */
        private String directory = "repository";
        private final JavaPropertiesBundleConfiguration javaProperties = new JavaPropertiesBundleConfiguration();
        private final JsonPropertiesBundleConfiguration jsonIcu = new JsonPropertiesBundleConfiguration();

        /**
         * The CRON expression specifying when the job synchronizing workspaces must be executed.
         */
        private String autoSyncFrequency = "0 0 1 * * *";

        public Repository(AppProperties appProperties) {
            this.appProperties = appProperties;
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
        public JavaPropertiesBundleConfiguration getJavaProperties() {
            return javaProperties;
        }

        /**
         * Returns the {@link BundleConfiguration configuration} like to JSON files.
         */
        @ConfigurationProperties(prefix = "json-icu")
        public JsonPropertiesBundleConfiguration getJsonIcu() {
            return jsonIcu;
        }
    }

    /**
     * Default configuration for translations bundles.
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static abstract class BundleConfiguration {

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
     * Default configuration for Java Properties translations bundles.
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class JavaPropertiesBundleConfiguration {

        /**
         * Use UTF-8 encoding for Java file.
         */
        private boolean utf8Encoding = true;

        /**
         * The property separator.
         */
        private String separator = "=";

    }

    /**
     * Default configuration for JSON Properties translations bundles.
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class JsonPropertiesBundleConfiguration {

    }

    /**
     * Properties related to the locking service.
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Lock {

        /**
         * The maximum time to get the lock (in milli-seconds) after a timeout exception will be thrown.
         */
        private int timeoutInMS = 120000;

    }

    /**
     * Properties related to scheduled tasks.
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class ScheduledTask {

        /**
         * The CRON expression specifying when the job cleaning old task executions must be executed.
         */
        private String cleanupFrequency = "0 0 6 * * *";

        /**
         * The number of days after which a task execution will be cleaned.
         */
        private int cleanupExecutionsOlderThanDays = 31;

        /**
         * The delay in minute after the task finishes it's execution. This prevents a bug in Spring, if the task
         * executes to fast then it's rescheduled 1sec after.
         */
        private int delayTaskExecutionInMin = 1;
    }
}
