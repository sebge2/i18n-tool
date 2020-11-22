package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.*;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import org.apache.commons.lang3.tuple.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;

/**
 * Handler of a particular kind of translation bundle file.
 *
 * @author Sebastien Gerard
 */
public interface BundleHandler {

    /**
     * Returns whether the specified {@link BundleType bundle type} is supported by this handler.
     */
    boolean support(BundleType bundleType);

    /**
     * Returns whether the specified directory can be scanned by the walker.
     */
    boolean continueScanning(File directory, BundleWalkingContext context);

    /**
     * Scans the specified directory and finds {@link ScannedBundleFile bundle files}.
     */
    Flux<ScannedBundleFile> scanBundles(File directory, BundleWalkingContext context);

    /**
     * Scans all the translations in the specified locale and associated to the specified
     * {@link ScannedBundleFileLocation bundle file location}. Translations are nullable and are associated to their bundle keys.
     */
    Flux<Pair<String, String>> scanTranslations(ScannedBundleFileLocation bundleFile,
                                                TranslationLocaleEntity locale,
                                                BundleWalkingContext context);

    /**
     * Writes all the translations into the specified locale and associated to the specified
     * {@link ScannedBundleFileLocation bundle file location}. Translations are nullable and are associated to their bundle keys.
     */
    Mono<Void> updateTranslations(ScannedBundleFileLocation bundleFile,
                                  TranslationLocaleEntity locale,
                                  Flux<Pair<String, String>> translations,
                                  TranslationRepositoryWriteApi repositoryAPI);

}
