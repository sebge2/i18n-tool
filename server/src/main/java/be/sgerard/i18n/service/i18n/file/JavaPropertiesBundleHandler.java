package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.BundleWalkContext;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileEntry;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileLocation;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import be.sgerard.i18n.service.workspace.WorkspaceException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;
import java.util.PropertyResourceBundle;

import static java.util.Collections.singleton;

/**
 * {@link BundleHandler Bundle handler} for bundle file based on Java properties.
 *
 * @author Sebastien Gerard
 */
@Component
public class JavaPropertiesBundleHandler implements BundleHandler {

    /**
     * File extension (including ".").
     */
    public static final String EXTENSION = ".properties";

    private static final Logger logger = LoggerFactory.getLogger(JavaPropertiesBundleHandler.class);

    public JavaPropertiesBundleHandler() {
    }

    @Override
    public boolean support(BundleType bundleType) {
        return bundleType == BundleType.JAVA_PROPERTIES;
    }

    @Override
    public boolean continueScanning(File directory, BundleWalkContext context) {
        return context.canWalkTrough(BundleType.JAVA_PROPERTIES, directory.toPath());
    }

    @Override
    public Flux<ScannedBundleFile> scanBundles(File directory, BundleWalkContext context) {
        return context
                .getApi()
                .listNormalFiles(directory)
                .flatMap(
                        file ->
                                findLocales(file, context)
                                        .map(locale ->
                                                Mono.just(
                                                        new ScannedBundleFile(
                                                                getBundleName(file, locale),
                                                                BundleType.JAVA_PROPERTIES,
                                                                directory,
                                                                singleton(new ScannedBundleFileEntry(locale, file))
                                                        )
                                                )
                                        )
                                        .orElseGet(Mono::empty)
                )
                .groupBy(ScannedBundleFile::getName)
                .flatMap(group -> group.reduce(ScannedBundleFile::merge));
    }

    @Override
    public Flux<Pair<String, String>> scanTranslations(ScannedBundleFileLocation bundleFile,
                                                       TranslationLocaleEntity locale,
                                                       BundleWalkContext context) {
        final File bundleFileEntry = getBundleFileEntry(bundleFile, locale);

        return context
                .getApi()
                .openInputStream(bundleFileEntry)
                .flatMapMany(inputStream ->
                        Mono
                                .just(readTranslations(bundleFileEntry, inputStream))
                                .flatMapMany(translations ->
                                        Flux.fromStream(
                                                translations.keySet().stream()
                                                        .map(key -> Pair.of(key, translations.getString(key)))
                                        )
                                )
                                .doOnTerminate(() -> {
                                    try {
                                        inputStream.close();
                                    } catch (Exception e) {
                                        logger.info("Error while closing stream.", e);
                                    }
                                })
                );
    }

    @Override
    public Mono<Void> updateTranslations(ScannedBundleFileLocation bundleFile,
                                         TranslationLocaleEntity locale,
                                         Flux<Pair<String, String>> translations,
                                         TranslationRepositoryWriteApi repositoryAPI) {
        final File bundleFileEntry = getBundleFileEntry(bundleFile, locale);

        return repositoryAPI
                .openAsTemp(bundleFileEntry)
                .flatMap(outputFile -> writeTranslations(translations, bundleFileEntry, outputFile));
    }

    /**
     * Finds the {@link TranslationLocaleEntity locale} from the specified file. If the file is not a bundle,
     * nothing is returned.
     */
    private Optional<TranslationLocaleEntity> findLocales(File file, BundleWalkContext context) {
        return context
                .getLocales()
                .stream()
                .filter(locale -> file.getName().endsWith(getSuffix(locale)))
                .findFirst();
    }

    /**
     * Returns the bundle name based on the specified bundle file entry and the locale.
     */
    private String getBundleName(File file, TranslationLocaleEntity locale) {
        return file.getName().substring(0, file.getName().length() - getSuffix(locale).length());
    }

    /**
     * Returns the suffix of the file for the specified locale.
     */
    private String getSuffix(TranslationLocaleEntity locale) {
        return "_" + locale.toLocale().toLanguageTag() + EXTENSION;
    }

    /**
     * Returns the location of the bundle file entry.
     */
    private File getBundleFileEntry(ScannedBundleFileLocation bundleFileLocation, TranslationLocaleEntity locale) {
        return new File(
                bundleFileLocation.getDirectory(),
                bundleFileLocation.getName() + getSuffix(locale)
        );
    }

    /**
     * Reads translations from the specified file using the specified stream.
     */
    private PropertyResourceBundle readTranslations(File file, InputStream fileStream) {
        try {
            return new PropertyResourceBundle(fileStream);
        } catch (Exception e) {
            throw WorkspaceException.onFileReading(file, e);
        }
    }

    /**
     * Writes the specified translations in the specified file using the output-stream.
     */
    private Mono<Void> writeTranslations(Flux<Pair<String, String>> translations, File file, File outputFile) {
        return Flux
                .using(
                        () -> new PropertiesConfiguration(outputFile),
                        conf -> translations.doOnNext(translation -> conf.setProperty(translation.getKey(), translation.getValue())),
                        conf -> {
                            try {
                                conf.save();
                            } catch (ConfigurationException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .onErrorResume(e -> Mono.error(WorkspaceException.onFileWriting(file, e)))
                .then();
    }
}
