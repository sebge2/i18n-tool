package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKeyDto;
import be.sgerard.i18n.service.repository.git.GitAPI;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Sebastien Gerard
 */
public interface TranslationBundleHandler {

    boolean support(ScannedBundleFileDto bundleFile);

    boolean continueScanning(File directory);

    Stream<ScannedBundleFileDto> scanBundles(File directory, GitAPI repositoryAPI) throws IOException;

    List<ScannedBundleFileKeyDto> scanKeys(ScannedBundleFileDto bundleFile, GitAPI repositoryAPI) throws IOException;

    void updateBundle(ScannedBundleFileDto bundleFile,
                      List<ScannedBundleFileKeyDto> keys,
                      GitAPI repositoryAPI) throws IOException;

}
