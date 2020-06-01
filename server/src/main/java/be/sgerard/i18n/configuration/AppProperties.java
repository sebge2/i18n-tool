package be.sgerard.i18n.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
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


    private int lockTimeoutInS = 120;
    private String javaTranslationBundleIgnoredPaths;
    private String jsonIcuTranslationBundleDirs;

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

    public int getLockTimeoutInS() {
        return lockTimeoutInS;
    }

    public void setLockTimeoutInS(int lockTimeoutInS) {
        this.lockTimeoutInS = lockTimeoutInS;
    }

    public String getJavaTranslationBundleIgnoredPaths() {
        return javaTranslationBundleIgnoredPaths;
    }

    public List<String> getJavaTranslationBundleIgnoredPathsAsList() {
        return getJavaTranslationBundleIgnoredPaths() != null
                ? Stream.of(getJavaTranslationBundleIgnoredPaths().split(",")).map(String::trim).collect(toList())
                : emptyList();
    }

    public void setJavaTranslationBundleIgnoredPaths(String javaTranslationBundleIgnoredPaths) {
        this.javaTranslationBundleIgnoredPaths = javaTranslationBundleIgnoredPaths;
    }

    public String getJsonIcuTranslationBundleDirs() {
        return jsonIcuTranslationBundleDirs;
    }

    public List<String> getJsonIcuTranslationBundleDirsAsList() {
        return getJsonIcuTranslationBundleDirs() != null
                ? Stream.of(getJsonIcuTranslationBundleDirs().split(",")).map(String::trim).collect(toList())
                : emptyList();
    }

    public void setJsonIcuTranslationBundleDirs(String jsonIcuTranslationBundleDirs) {
        this.jsonIcuTranslationBundleDirs = jsonIcuTranslationBundleDirs;
    }

    /**
     * Properties related to directories.
     */
    @ConfigurationProperties(prefix = "repository")
    public static final class Repository {

        private final AppProperties appProperties;

        private String directory = "repository";

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
    }

    /**
     * Properties related to security.
     */
    @ConfigurationProperties(prefix = "security")
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
}
