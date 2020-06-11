package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKeyDto;
import be.sgerard.i18n.service.i18n.TranslationRepositoryReadApi;

import java.io.File;
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

    public void walk(TranslationRepositoryReadApi browseAPI, TranslationBundleConsumer consumer) {
        walk(new File("/"), browseAPI, consumer, handlers);
    }

    private void walk(File directory,
                      TranslationRepositoryReadApi browseAPI,
                      TranslationBundleConsumer consumer,
                      List<TranslationBundleHandler> handlers) {
        final List<TranslationBundleHandler> updatedHandlers = handlers.stream()
                .filter(handler -> handler.continueScanning(directory))
                .collect(toList());

        if (updatedHandlers.isEmpty()) {
            return;
        }

        for (TranslationBundleHandler handler : updatedHandlers) {
//            handler.scanBundles(directory, browseAPI)
//                    .forEach(bundle -> {
//                        consumer.onBundleFound(bundle, handler.scanKeys(bundle, browseAPI));
//                    });
        }

//            browseAPI.listDirectories(directory)
//                    .forEach(subDir -> {
//                        try {
//                            walk(subDir, browseAPI, consumer, updatedHandlers);
//                        } catch (IOException e) {
//                            throw new WrappedIOException(e);
//                        }
//                    });
    }

    public interface TranslationBundleConsumer {

        void onBundleFound(ScannedBundleFileDto bundleFile, List<ScannedBundleFileKeyDto> keys);

    }

}
