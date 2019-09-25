package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKeyDto;
import be.sgerard.i18n.service.git.RepositoryAPI;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Sebastien Gerard
 */
@Component
public class JsonICUTranslationBundleHandler implements TranslationBundleHandler {

    private final List<String> pathToIgnores;
    private final AntPathMatcher antPathMatcher;

    public JsonICUTranslationBundleHandler(AppProperties appProperties) {
        this.pathToIgnores = appProperties.getJsonIcuTranslationBundleDirsAsList();
        this.antPathMatcher = new AntPathMatcher();
    }

    @Override
    public boolean support(ScannedBundleFileDto bundleFile) {
        return bundleFile.getType() == BundleType.JSON_ICU;
    }

    @Override
    public boolean continueScanning(File directory) {
        return pathToIgnores.stream().anyMatch(dirPathPattern -> antPathMatcher.match(dirPathPattern, directory.toPath().toString()));
    }

    @Override
    public Stream<ScannedBundleFileDto> scanBundles(File directory, RepositoryAPI repositoryAPI) throws IOException {
        return null;
    }

    @Override
    public Collection<ScannedBundleFileKeyDto> scanKeys(ScannedBundleFileDto bundleFile, RepositoryAPI repositoryAPI) throws IOException {
        return null;
    }

    @Override
    public void updateBundle(ScannedBundleFileDto bundleFile, Collection<ScannedBundleFileKeyDto> keys, RepositoryAPI repositoryAPI) throws IOException {

    }
}
