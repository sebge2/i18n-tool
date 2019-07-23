package be.sgerard.poc.githuboauth.service.i18n.file;

import be.sgerard.poc.githuboauth.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.poc.githuboauth.model.i18n.file.ScannedBundleFileKeyDto;
import be.sgerard.poc.githuboauth.service.git.RepositoryAPI;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Sebastien Gerard
 */
public interface TranslationBundleHandler {

    boolean support(ScannedBundleFileDto bundleFile);

    boolean continueScanning(File directory);

    Stream<ScannedBundleFileDto> scanBundles(File directory, RepositoryAPI repositoryAPI) throws IOException;

    Stream<ScannedBundleFileKeyDto> scanKeys(ScannedBundleFileDto bundleFile, RepositoryAPI repositoryAPI) throws IOException;

    void updateBundle(ScannedBundleFileDto bundleFile,
                      Supplier<Stream<ScannedBundleFileKeyDto>> translationsProvider,
                      RepositoryAPI repositoryAPI) throws IOException;

}
