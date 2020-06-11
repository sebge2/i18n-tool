package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKeyDto;
import be.sgerard.i18n.service.i18n.TranslationRepositoryReadApi;
import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
    public Stream<ScannedBundleFileDto> scanBundles(File directory, TranslationRepositoryReadApi repositoryAPI) {

        return Stream.empty();
/*        return repositoryAPI.listNormalFiles(directory)
                .map(
                        file -> {
                            final Matcher matcher = BUNDLE_PATTERN.matcher(file.getName());

                            if (matcher.matches()) {
                                return new ScannedBundleFileDto(
                                        matcher.group(1),
                                        BundleType.JAVA,
                                        directory,
                                        singletonList(Locale.forLanguageTag(matcher.group(2))),
                                        singletonList(file)
                                );
                            } else {
                                return null;
                            }
                        }
                )
                .filter(Objects::nonNull)
                .collect(groupingBy(ScannedBundleFileDto::getName, reducing(null, ScannedBundleFileDto::merge)))
                .values()
                .stream()*/
    }

    @Override
    public List<ScannedBundleFileKeyDto> scanKeys(ScannedBundleFileDto bundleFile, TranslationRepositoryReadApi repositoryAPI) {
        return Collections.emptyList();
//            return new ArrayList<>(
//                    bundleFile.getFiles().stream()
//                            .flatMap(
//                                    file -> {
//                                        try { // TODO language empty
//                                            final PropertyResourceBundle resourceBundle = new PropertyResourceBundle(repositoryAPI.openInputStream(file));
//
//                                            return resourceBundle.keySet().stream()
//                                                    .map(key -> new ScannedBundleFileKeyDto(key, singletonMap(getLocale(file), mapToNullIfEmpty(resourceBundle.getString(key)))));
//                                        } catch (IOException e) {
//                                            throw new WrappedIOException(e);
//                                        }
//                                    }
//                            )
//                            .collect(groupingBy(ScannedBundleFileKeyDto::getKey, reducing(null, ScannedBundleFileKeyDto::merge)))
//                            .values()
//            );
    }

    @Override
    public void updateBundle(ScannedBundleFileDto bundleFile,
                             List<ScannedBundleFileKeyDto> keys,
                             TranslationRepositoryWriteApi repositoryAPI) {
//        try {
        for (File file : bundleFile.getFiles()) {
            final Matcher matcher = BUNDLE_PATTERN.matcher(file.getName());

            if (!matcher.matches()) {
                continue;
            }

//                final PropertiesConfiguration conf = new PropertiesConfiguration(repositoryAPI.openAsTemp(file));
//
//                final Locale locale = getLocale(file);
//// TODO only what changed ?
//                keys.forEach(key -> conf.setProperty(key.getKey(), key.getTranslations().getOrDefault(locale, null)));
//
//                conf.save();
        }
//        } catch (ConfigurationException e) {
//            throw new IOException("Error while updating bundle files.", e);
//        }
    }

    private Locale getLocale(File file) {
        final Matcher matcher = BUNDLE_PATTERN.matcher(file.getName());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("The file [" + file + "] is not a resource bundle file.");
        }

        return Locale.forLanguageTag(matcher.group(2));
    }
}
