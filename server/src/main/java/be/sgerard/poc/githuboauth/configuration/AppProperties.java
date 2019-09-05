package be.sgerard.poc.githuboauth.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * @author Sebastien Gerard
 */
@ConfigurationProperties(prefix = "poc")
public class AppProperties {

    private String baseDirectory;
    private String repoUserName;
    private String repoName;
    private int lockTimeoutInS = 120;
    private String javaTranslationBundleIgnoredPaths;
    private String gitHubWebhookSecret = "";
    private Set<Locale> locales = emptySet();

    public AppProperties() {
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public File getRepositoryLocation(){
        return new File(getBaseDirectory(), "sandbox");
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

    public String getGitHubWebhookSecret() {
        return gitHubWebhookSecret;
    }

    public void setGitHubWebhookSecret(String gitHubWebhookSecret) {
        this.gitHubWebhookSecret = gitHubWebhookSecret;
    }

    public Set<Locale> getLocales() {
        return locales;
    }

    public void setLocales(Set<String> locales) {
        this.locales = locales.stream().map(Locale::forLanguageTag).collect(toSet());
    }
}
