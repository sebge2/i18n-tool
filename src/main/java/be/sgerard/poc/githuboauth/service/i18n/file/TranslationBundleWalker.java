package be.sgerard.poc.githuboauth.service.i18n.file;

import be.sgerard.poc.githuboauth.model.i18n.file.TranslationBundleFileDto;
import be.sgerard.poc.githuboauth.model.i18n.file.TranslationFileEntryDto;
import com.fasterxml.jackson.datatype.jdk8.WrappedIOException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static be.sgerard.poc.githuboauth.service.i18n.file.TranslationFileUtils.listFiles;
import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
@Service
public class TranslationBundleWalker {

    private final List<TranslationBundleHandler> handlers;

    public TranslationBundleWalker(List<TranslationBundleHandler> handlers) {
        this.handlers = handlers;
    }

    public void walk(File directory, TranslationBundleConsumer consumer) throws IOException {
        walk(directory, consumer, handlers);
    }

    private void walk(File directory,
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
                handler.scanBundles(directory)
                        .forEach(bundle -> {
                            try {
                                consumer.onBundleFound(bundle, handler.getEntries(bundle));
                            } catch (IOException e) {
                                throw new WrappedIOException(e);
                            }
                        });
            }

            listFiles(directory)
                    .filter(File::isDirectory)
                    .forEach(subDir -> {
                        try {
                            walk(subDir, consumer, updatedHandlers);
                        } catch (IOException e) {
                            throw new WrappedIOException(e);
                        }
                    });
        } catch (WrappedIOException e) {
            throw e.getCause();
        }
    }

    public interface TranslationBundleConsumer {

        void onBundleFound(TranslationBundleFileDto bundleFile, Stream<TranslationFileEntryDto> entries);

    }

}
