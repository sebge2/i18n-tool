package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.BundleWalkContext;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKey;
import be.sgerard.i18n.service.i18n.TranslationRepositoryReadApi;
import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

/**
 * Handler of a particular kind of translation bundle file.
 *
 * @author Sebastien Gerard
 */
public interface TranslationBundleHandler {

    /**
     * Returns whether the specified {@link BundleType bundle type} is supported by this handler.
     */
    boolean support(BundleType bundleType);

    /**
     * Returns whether the specified directory can be scanned by the walker.
     */
    boolean continueScanning(File directory, BundleWalkContext context);

    Flux<ScannedBundleFile> scanBundles(File directory, TranslationRepositoryReadApi api);

    List<ScannedBundleFileKey> scanKeys(ScannedBundleFile bundleFile, TranslationRepositoryReadApi api);

    void updateBundle(ScannedBundleFile bundleFile,
                      List<ScannedBundleFileKey> keys,
                      TranslationRepositoryWriteApi repositoryAPI);

}
