package be.sgerard.poc.githuboauth.service.i18n.file;

import be.sgerard.poc.githuboauth.model.i18n.file.TranslationBundleFileDto;
import be.sgerard.poc.githuboauth.model.i18n.file.TranslationFileEntryDto;
import be.sgerard.poc.githuboauth.service.git.BranchBrowsingAPI;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * @author Sebastien Gerard
 */
public interface TranslationBundleHandler {

    boolean continueScanning(File directory);

    Stream<TranslationBundleFileDto> scanBundles(File directory, BranchBrowsingAPI browseAPI) throws IOException;

    Stream<TranslationFileEntryDto> getEntries(TranslationBundleFileDto bundleFile, BranchBrowsingAPI browseAPI) throws IOException;

}
