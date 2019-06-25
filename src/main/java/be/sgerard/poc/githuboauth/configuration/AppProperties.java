package be.sgerard.poc.githuboauth.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
@ConfigurationProperties(prefix = "poc")
public class AppProperties {

    private String localRepositoryLocation;
    private String repoUserName;
    private String repoName;
    private int lockTimeoutInS = 120;
    private String javaTranslationBundleIgnoredPaths;

    public AppProperties() {
    }

    public String getLocalRepositoryLocation() {
        return localRepositoryLocation;
    }

    public void setLocalRepositoryLocation(String localRepositoryLocation) {
        this.localRepositoryLocation = localRepositoryLocation;
    }

    public String getRepoUserName() {
        return repoUserName;
    }

    public void setRepoUserName(String repoUserName) {
        this.repoUserName = repoUserName;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getRepoFqnName() {
        return getRepoUserName() + "/" + getRepoName();
    }

    public String getRepoCheckoutUri() {
        return "https://github.com/" + getRepoUserName() + "/" + getRepoName() + ".git";
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
}
