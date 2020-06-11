package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKeyDto;
import be.sgerard.i18n.service.i18n.TranslationRepositoryReadApi;
import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Sebastien Gerard
 */
public interface TranslationBundleHandler {

    boolean support(ScannedBundleFileDto bundleFile);

    boolean continueScanning(File directory);

    Stream<ScannedBundleFileDto> scanBundles(File directory, TranslationRepositoryReadApi repositoryAPI);

    List<ScannedBundleFileKeyDto> scanKeys(ScannedBundleFileDto bundleFile, TranslationRepositoryReadApi repositoryAPI);

    void updateBundle(ScannedBundleFileDto bundleFile,
                      List<ScannedBundleFileKeyDto> keys,
                      TranslationRepositoryWriteApi repositoryAPI);

}
