package be.sgerard.i18n.service.dictionary;

import be.sgerard.i18n.model.dictionary.persistence.DictionaryEntryEntity;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.locale.TranslationLocaleManager;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.StringUtils.isEmpty;
import static org.springframework.util.StringUtils.trimWhitespace;

/**
 * Import a dictionaries from a CSV file.
 *
 * @author Sebastien Gerard
 */
@Component
public class DictionaryImporter {

    private final TranslationLocaleManager localeManager;
    private final DictionaryManager dictionaryManager;

    public DictionaryImporter(TranslationLocaleManager localeManager, DictionaryManager dictionaryManager) {
        this.localeManager = localeManager;
        this.dictionaryManager = dictionaryManager;
    }

    /**
     * Imports the specified stream containing the CSV file.
     */
    public Flux<DictionaryEntryEntity> importFromCsv(InputStream stream, boolean persist) {
        final CSVReader reader = new CSVReader(new InputStreamReader(stream));
        final Iterator<String[]> lineIterator = reader.iterator();

        if (!lineIterator.hasNext()) {
            return Flux.empty();
        }

        return initializeMappings(lineIterator.next())
                .flatMapMany(mappings ->
                        Flux
                                .fromStream(
                                        Stream.generate(() -> null)
                                                .takeWhile(x -> lineIterator.hasNext())
                                                .map(x -> lineIterator.next())
                                )
                                .flatMap(record -> Mono.justOrEmpty(mappings.extractDictionaryEntry(record)))
                )
                .flatMap(entry -> persist ? dictionaryManager.create(entry) : Mono.just(entry));
    }

    /**
     * Initializes {@link ImportMappings mappings} based on the specified CSV header.
     */
    private Mono<ImportMappings> initializeMappings(String[] header) {
        return localeManager.findAll().collectList()
                .map(availableLocales -> initializeMappings(header, availableLocales));
    }

    /**
     * Initializes {@link ImportMappings mappings} based on the specified CSV header.
     */
    private ImportMappings initializeMappings(String[] header, List<TranslationLocaleEntity> availableLocales) {
        final List<String> headerList = Stream.of(header).map(String::trim).map(String::toLowerCase).collect(toList());

        final Map<Integer, TranslationLocaleEntity> mapping = availableLocales.stream()
                .map(availableLocale -> {
                    final int headerIndex = findHeaderIndex(headerList, availableLocale);

                    if (headerIndex < 0) {
                        return null;
                    }

                    return new AbstractMap.SimpleEntry<>(headerIndex, availableLocale);
                })
                .filter(Objects::nonNull)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new ImportMappings(mapping);
    }

    /**
     * Finds the index of the header associated to the specified {@link TranslationLocaleEntity locale}. The column header
     * may contain the locale in its String representation, or its display name.
     */
    private int findHeaderIndex(List<String> headerList, TranslationLocaleEntity availableLocale) {
        int headerIndex = headerList.indexOf(availableLocale.toLocale().toString().toLowerCase());

        if (headerIndex >= 0) {
            return headerIndex;
        }

        return availableLocale.getDisplayName()
                .map(String::toLowerCase)
                .map(headerList::indexOf)
                .orElse(-1);
    }

    /**
     * Mappings associating a CSV column to an internal {@link TranslationLocaleEntity locale}.
     */
    private static final class ImportMappings {

        private final Map<Integer, TranslationLocaleEntity> mapping;

        private ImportMappings(Map<Integer, TranslationLocaleEntity> mapping) {
            this.mapping = mapping;
        }

        /**
         * Extract a {@link DictionaryEntryEntity dictionary entry} from the specified CSV line.
         */
        public Optional<DictionaryEntryEntity> extractDictionaryEntry(String[] line) {
            final Map<String, String> translations = mapping.entrySet().stream()
                    .filter(entry -> entry.getKey() < line.length)
                    .peek(entry -> line[entry.getKey()] = trimWhitespace(line[entry.getKey()]))
                    .filter(entry -> !isEmpty(line[entry.getKey()]))
                    .map(entry -> new AbstractMap.SimpleEntry<>(entry.getValue().getId(), line[entry.getKey()]))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (translations.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(
                    new DictionaryEntryEntity(translations)
            );
        }
    }
}
