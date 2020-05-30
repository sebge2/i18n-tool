package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKeyDto;
import be.sgerard.i18n.service.i18n.TranslationRepositoryApi;
import com.fasterxml.jackson.datatype.jdk8.WrappedIOException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
public class TranslationBundleWalker {

    private final List<TranslationBundleHandler> handlers;

    public TranslationBundleWalker(List<TranslationBundleHandler> handlers) {
        this.handlers = handlers;
    }

    public void walk(GitAPI browseAPI, TranslationBundleConsumer consumer) throws IOException {
        walk(new File("/"), browseAPI, consumer, handlers);
    }

    private void walk(File directory,
                      GitAPI browseAPI,
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

        void onBundleFound(ScannedBundleFileDto bundleFile, List<ScannedBundleFileKeyDto> keys);

    }

}
