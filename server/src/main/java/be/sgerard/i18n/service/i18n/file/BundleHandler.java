package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.BundleWalkContext;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKey;
import be.sgerard.i18n.model.i18n.file.ScannedBundleTranslation;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileEntry;
import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.List;

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
    boolean continueScanning(File directory, BundleWalkContext context);

    /**
     * Scans the specified directory and finds {@link ScannedBundleFile bundle files}.
     */
    Flux<ScannedBundleFile> scanBundles(File directory, BundleWalkContext context);

    /**
     * Scans all the {@link ScannedBundleTranslation translations} of all the {@link ScannedBundleFileEntry entries}
     * composing the specified {@link ScannedBundleFile bundle file}.
     */
    Flux<ScannedBundleTranslation> scanTranslations(ScannedBundleFile bundleFile, BundleWalkContext context);

    /**
     * Writes the specified {@link ScannedBundleFileKey translation keys} into the specified {@link ScannedBundleFile bundle file}.
     */
    Mono<Void> updateBundle(ScannedBundleFile bundleFile,
                            List<ScannedBundleFileKey> keys,
                            TranslationRepositoryWriteApi repositoryAPI);

}
