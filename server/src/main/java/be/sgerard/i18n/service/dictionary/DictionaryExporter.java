package be.sgerard.i18n.service.dictionary;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.locale.TranslationLocaleManager;
import com.opencsv.CSVWriter;
import org.springframework.core.io.buffer.*;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.List;
import java.util.Objects;

import static be.sgerard.i18n.service.snapshot.SnapshotManagerImpl.BUFFER_SIZE;

/**
 * Export a dictionary in a CSV file.
 *
 * @author Sebastien Gerard
 */
@Component
public class DictionaryExporter {

    /**
     * Default name of the exported CSV file.
     */
    public static final String CSV_FILE_NAME = "i18n_tool-dictionary.csv";

    private final TranslationLocaleManager localeManager;
    private final DictionaryManager dictionaryManager;

    public DictionaryExporter(TranslationLocaleManager localeManager, DictionaryManager dictionaryManager) {
        this.localeManager = localeManager;
        this.dictionaryManager = dictionaryManager;
    }

    /**
     * Exports the dictionary into a CSV file. The first element of the pair, is the original file name, the second
     * is the file content.
     */
    public Mono<Pair<String, Flux<String>>> exportToCsv() {
        return localeManager.findAll()
                .collectList()
                .map(availableLocales ->
                        Pair.of(CSV_FILE_NAME, exportEntries(availableLocales))
                );
    }

    /**
     * Exports the all entries (and the header) line by line.
     */
    private Flux<String> exportEntries(List<TranslationLocaleEntity> availableLocales) {
        return Flux
                .merge(
                        headerToFlux(availableLocales),
                        entriesToFlux(availableLocales)
                )
                .map(line -> {
                    // NICE may be a better way to stream the content
                    final StringWriter writer = new StringWriter();
                    final CSVWriter csvWriter = new CSVWriter(writer);

                    csvWriter.writeNext(line);

                    return writer.toString();
                });
    }

    /**
     * Exports the header (single flux element).
     */
    @SuppressWarnings("RedundantCast")
    private Flux<String[]> headerToFlux(List<TranslationLocaleEntity> availableLocales) {
        return Flux.<String[]>just(
                (String[]) availableLocales.stream()
                        .map(TranslationLocaleEntity::toLocale)
                        .map(Objects::toString)
                        .toArray(String[]::new)
        );
    }

    /**
     * Exports the all entries line by line.
     */
    private Flux<String[]> entriesToFlux(List<TranslationLocaleEntity> availableLocales) {
        return dictionaryManager.findAll()
                .map(entry ->
                        availableLocales.stream()
                                .map(entry::getTranslation)
                                .map(translation -> translation.orElse(""))
                                .toArray(String[]::new)
                );
    }
}