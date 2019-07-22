package be.sgerard.poc.githuboauth.service.i18n.file;

import be.sgerard.poc.githuboauth.configuration.AppProperties;
import be.sgerard.poc.githuboauth.model.i18n.BundleType;
import be.sgerard.poc.githuboauth.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.poc.githuboauth.model.i18n.file.ScannedBundleFileKeyDto;
import be.sgerard.poc.githuboauth.service.git.RepositoryAPI;
import com.fasterxml.jackson.datatype.jdk8.WrappedIOException;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static be.sgerard.poc.githuboauth.service.i18n.file.TranslationFileUtils.mapToNullIfEmpty;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

/**
 * @author Sebastien Gerard
 */
@Component
public class JavaTranslationBundleHandler implements TranslationBundleHandler {

    private static final Pattern BUNDLE_PATTERN = Pattern.compile("^(.+)_([a-z]{2}(_[A-Z]{2})?)\\.properties$");

    private final List<String> pathToIgnores;
    private final AntPathMatcher antPathMatcher;

    public JavaTranslationBundleHandler(AppProperties appProperties) {
        this.pathToIgnores = appProperties.getJavaTranslationBundleIgnoredPathsAsList();
        this.antPathMatcher = new AntPathMatcher();
    }

    @Override
    public boolean support(ScannedBundleFileDto bundleFile) {
        return bundleFile.getType() == BundleType.JAVA;
    }

    @Override
    public boolean continueScanning(File directory) {
        return pathToIgnores.stream().noneMatch(ignoredPathPattern -> antPathMatcher.match(ignoredPathPattern, directory.toPath().toString()));
    }

    @Override
    public Stream<ScannedBundleFileDto> scanBundles(File directory, RepositoryAPI repositoryAPI) throws IOException {
        return repositoryAPI.listNormalFiles(directory)
                .map(
                        file -> {
                            final Matcher matcher = BUNDLE_PATTERN.matcher(file.getName());

                            if (matcher.matches()) {
                                return new ScannedBundleFileDto(matcher.group(1), BundleType.JAVA, directory, singletonList(file));
                            } else {
                                return null;
                            }
                        }
                )
                .filter(Objects::nonNull)
                .collect(groupingBy(ScannedBundleFileDto::getName, reducing(null, ScannedBundleFileDto::merge)))
                .values()
                .stream();
    }

    @Override
    public Stream<ScannedBundleFileKeyDto> scanKeys(ScannedBundleFileDto bundleFile, RepositoryAPI repositoryAPI) throws IOException {
        try {
            return bundleFile.getFiles().stream()
                    .flatMap(
                            file -> {
                                try {
                                    final PropertyResourceBundle resourceBundle = new PropertyResourceBundle(repositoryAPI.openFile(file));

                                    return resourceBundle.keySet().stream()
                                            .map(key -> new ScannedBundleFileKeyDto(key, singletonMap(getLocale(file), mapToNullIfEmpty(resourceBundle.getString(key)))));
                                } catch (IOException e) {
                                    throw new WrappedIOException(e);
                                }
                            }
                    )
                    .collect(groupingBy(ScannedBundleFileKeyDto::getKey, reducing(null, ScannedBundleFileKeyDto::merge)))
                    .values()
                    .stream();
        } catch (WrappedIOException e) {
            throw e.getCause();
        }
    }

    private Locale getLocale(File file) {
        final Matcher matcher = BUNDLE_PATTERN.matcher(file.getName());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("The file [" + file + "] is not a resource bundle file.");
        }

        return Locale.forLanguageTag(matcher.group(2));
    }
}
