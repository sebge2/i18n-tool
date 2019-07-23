package be.sgerard.poc.githuboauth.service.i18n.file;

import be.sgerard.poc.githuboauth.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.poc.githuboauth.model.i18n.file.ScannedBundleFileKeyDto;
import be.sgerard.poc.githuboauth.service.git.RepositoryAPI;
import com.fasterxml.jackson.datatype.jdk8.WrappedIOException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
public class TranslationBundleWalker {

    private final List<TranslationBundleHandler> handlers;

    public TranslationBundleWalker(List<TranslationBundleHandler> handlers) {
        this.handlers = handlers;
    }

    public void walk(RepositoryAPI browseAPI, TranslationBundleConsumer consumer) throws IOException {
        walk(new File("/"), browseAPI, consumer, handlers);
    }

    private void walk(File directory,
                      RepositoryAPI browseAPI,
                      TranslationBundleConsumer consumer,
                      List<TranslationBundleHandler> handlers) throws IOException {
        final List<TranslationBundleHandler> updatedHandlers = handlers.stream()
                .filter(handler -> handler.continueScanning(directory))
                .collect(toList());

        if (updatedHandlers.isEmpty()) {
            return;
        }

        try {
            for (TranslationBundleHandler handler : updatedHandlers) {
                handler.scanBundles(directory, browseAPI)
                        .forEach(bundle -> {
                            try {
                                consumer.onBundleFound(bundle, handler.scanKeys(bundle, browseAPI));
                            } catch (IOException e) {
                                throw new WrappedIOException(e);
                            }
                        });
            }

            browseAPI.listDirectories(directory)
                    .forEach(subDir -> {
                        try {
                            walk(subDir, browseAPI, consumer, updatedHandlers);
                        } catch (IOException e) {
                            throw new WrappedIOException(e);
                        }
                    });
        } catch (WrappedIOException e) {
            throw e.getCause();
        }
    }

    public interface TranslationBundleConsumer {

        void onBundleFound(ScannedBundleFileDto bundleFile, Stream<ScannedBundleFileKeyDto> entries);

    }

}
