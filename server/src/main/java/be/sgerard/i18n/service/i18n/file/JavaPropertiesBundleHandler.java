package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.BundleWalkContext;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKey;
import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import be.sgerard.i18n.service.workspace.WorkspaceException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static be.sgerard.i18n.service.i18n.file.TranslationFileUtils.mapToNullIfEmpty;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

/**
 * {@link BundleHandler Bundle handler} for bundle file based on Java properties.
 *
 * @author Sebastien Gerard
 */
@Component
public class JavaPropertiesBundleHandler implements BundleHandler {

    /**
     * Pattern for a translation bundle file. It's composed of the bundle name, the language (2 letters in lower case) and then
     * the region (2 letters in upper-case)
     */
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
    public Flux<ScannedBundleFileKey> scanKeys(ScannedBundleFile bundleFile, BundleWalkContext context) {
        return Flux
                .fromStream(bundleFile.getFiles().stream())
                .flatMap(file ->
                        context
                                .getApi()
                                .openInputStream(file)
                                .map(inputStream -> readTranslations(file, inputStream))
                                .flatMapMany(translations ->
                                        Flux.fromStream(
                                                translations.keySet().stream()
                                                        .map(key -> new ScannedBundleFileKey(key, singletonMap(getLocale(file), mapToNullIfEmpty(translations.getString(key)))))
                                        )
                                )
                )
                .groupBy(ScannedBundleFileKey::getKey)
                .flatMap(group -> group.reduce(ScannedBundleFileKey::merge));
    }

    @Override
    public Mono<Void> updateBundle(ScannedBundleFile bundleFile,
                                   List<ScannedBundleFileKey> keys,
                                   TranslationRepositoryWriteApi repositoryAPI) {
        return Flux
                .fromIterable(bundleFile.getFiles())
                .filter(file -> BUNDLE_PATTERN.matcher(file.getName()).matches())
                .flatMap(file ->
                        repositoryAPI
                                .openAsTemp(file)
                                .doOnNext(outputStream -> writeTranslations(keys, file, outputStream))
                )
                .then();
    }

    /**
     * Returns the locale based on the file name.
     *
     * @see #BUNDLE_PATTERN
     */
    private Locale getLocale(File file) {
        final Matcher matcher = BUNDLE_PATTERN.matcher(file.getName());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("The file [" + file + "] is not a resource bundle file.");
        }

        return Locale.forLanguageTag(matcher.group(2));
    }

    /**
     * Reads translations from the specified file using the specified stream.
     */
    private PropertyResourceBundle readTranslations(File file, InputStream fileStream) {
        try {
            return new PropertyResourceBundle(fileStream);
        } catch (IOException e) {
            throw WorkspaceException.onFileReading(file, e);
        }
    }

    /**
     * Writes the specified keys in the specified file using the output-stream.
     */
    private void writeTranslations(List<ScannedBundleFileKey> keys, File file, File outputStream) {
        try {
            final Locale locale = getLocale(file);

            final PropertiesConfiguration conf = new PropertiesConfiguration(outputStream);

            keys.forEach(key -> conf.setProperty(key.getKey(), key.getTranslations().getOrDefault(locale, null)));

            conf.save();
        } catch (ConfigurationException e) {
            throw WorkspaceException.onFileWriting(file, e);
        }
    }
}
