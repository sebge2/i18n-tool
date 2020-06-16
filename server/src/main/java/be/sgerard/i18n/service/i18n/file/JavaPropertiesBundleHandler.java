package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.BundleWalkContext;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKey;
import be.sgerard.i18n.service.i18n.TranslationRepositoryReadApi;
import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

/**
 * @author Sebastien Gerard
 */
@Component
public class JavaPropertiesBundleHandler implements BundleHandler {

    private static final Pattern BUNDLE_PATTERN = Pattern.compile("^(.+)_([a-z]{2}(_[A-Z]{2})?)\\.properties$");

    public JavaPropertiesBundleHandler() {
    }

    @Override
    public boolean support(BundleType bundleType) {
        return bundleType == BundleType.JAVA_PROPERTIES;
    }

    @Override
    public boolean continueScanning(File directory, BundleWalkContext context) {
        return context.isIncluded(BundleType.JAVA_PROPERTIES, directory.toPath());
    }

    @Override
    public Flux<ScannedBundleFile> scanBundles(File directory, BundleWalkContext context) {
        return context
                .getApi()
                .listNormalFiles(directory)
                .flatMap(
                        file -> {
                            final Matcher matcher = BUNDLE_PATTERN.matcher(file.getName());
                            if (matcher.matches()) {
                                return Mono.just(
                                        new ScannedBundleFile(
                                                matcher.group(1),
                                                BundleType.JAVA_PROPERTIES,
                                                directory,
                                                singletonList(Locale.forLanguageTag(matcher.group(2))),
                                                singletonList(file)
                                        )
                                );
                            } else {
                                return Mono.empty();
                            }
                        }
                )
                .filter(bundleFile -> context.getLocales().containsAll(bundleFile.getLocales()))
                .groupBy(ScannedBundleFile::getName)
                .flatMap(group -> group.reduce(ScannedBundleFile::merge));
    }

    @Override
    public List<ScannedBundleFileKey> scanKeys(ScannedBundleFile bundleFile, TranslationRepositoryReadApi api) {
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
    public void updateBundle(ScannedBundleFile bundleFile,
                             List<ScannedBundleFileKey> keys,
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
