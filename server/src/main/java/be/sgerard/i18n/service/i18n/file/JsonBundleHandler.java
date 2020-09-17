package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.BundleWalkingContext;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileEntry;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileLocation;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import be.sgerard.i18n.service.workspace.WorkspaceException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static be.sgerard.i18n.service.i18n.file.TranslationFileUtils.mapToNullIfEmpty;
import static java.util.Collections.singleton;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * {@link BundleHandler Bundle handler} for JSON files.
 *
 * @author Sebastien Gerard
 */
@Component
public class JsonBundleHandler implements BundleHandler {

    /**
     * File extension (including ".").
     */
    public static final String EXTENSION = ".json";

    private static final Logger logger = LoggerFactory.getLogger(JsonBundleHandler.class);

    private final ObjectMapper objectMapper;

    public JsonBundleHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public JsonBundleHandler() {
        this(initDefaultMapper());
    }

    @Override
    public boolean support(BundleType bundleType) {
        return bundleType == BundleType.JSON_ICU;
    }

    @Override
    public boolean continueScanning(File directory, BundleWalkingContext context) {
        return context.canWalkTrough(BundleType.JSON_ICU, directory.toPath());
    }

    @Override
    public Flux<ScannedBundleFile> scanBundles(File directory, BundleWalkingContext context) {
        return context
                .getApi()
                .listNormalFiles(directory)
                .flatMap(
                        file ->
                                findLocales(file, context)
                                        .map(locale ->
                                                Mono.just(
                                                        new ScannedBundleFile(
                                                                directory.getName(),
                                                                BundleType.JSON_ICU,
                                                                directory,
                                                                singleton(new ScannedBundleFileEntry(locale, file))
                                                        )
                                                ))
                                        .orElseGet(Mono::empty)
                )
                .groupBy(ScannedBundleFile::getName)
                .flatMap(group -> group.reduce(ScannedBundleFile::merge));
    }

    @Override
    public Flux<Pair<String, String>> scanTranslations(ScannedBundleFileLocation bundleFile,
                                                       TranslationLocaleEntity locale,
                                                       BundleWalkingContext context) {
        final File bundleFileEntry = getBundleFileEntry(bundleFile, locale);

        return context
                .getApi()
                .openInputStream(bundleFileEntry)
                .flatMapMany(inputStream ->
                        Mono
                                .just(readTranslations(bundleFileEntry, inputStream))
                                .flatMapMany(translations ->
                                        inlineValues(translations)
                                                .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
                                )
                                .doOnTerminate(() -> {
                                    try {
                                        inputStream.close();
                                    } catch (Exception e) {
                                        logger.info("Error while closing stream.", e);
                                    }
                                })
                );
    }

    @Override
    public Mono<Void> updateTranslations(ScannedBundleFileLocation bundleFile,
                                         TranslationLocaleEntity locale,
                                         Flux<Pair<String, String>> translations,
                                         TranslationRepositoryWriteApi repositoryAPI) {
        final File bundleFileEntry = getBundleFileEntry(bundleFile, locale);

        return repositoryAPI
                .openAsTemp(bundleFileEntry)
                .flatMap(outputFile -> writeTranslations(translations, bundleFileEntry, outputFile));
    }

    /**
     * Returns the {@link ObjectMapper object mapper} to use by default.
     */
    private static ObjectMapper initDefaultMapper() {
        return new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .setDefaultPrettyPrinter(new CustomPrettyPrinter());
    }

    /**
     * Finds the {@link TranslationLocaleEntity locale} from the specified file. If the file is not a bundle,
     * nothing is returned.
     */
    private Optional<TranslationLocaleEntity> findLocales(File file, BundleWalkingContext context) {
        return context
                .getLocales()
                .stream()
                .filter(locale -> Objects.equals(file.getName(), getFileName(locale)))
                .findFirst();
    }

    /**
     * Returns the suffix of the file for the specified locale.
     */
    private String getFileName(TranslationLocaleEntity locale) {
        return locale.toLocale().toLanguageTag() + EXTENSION;
    }

    /**
     * Returns the location of the bundle file entry.
     */
    private File getBundleFileEntry(ScannedBundleFileLocation bundleFileLocation, TranslationLocaleEntity locale) {
        return new File(
                bundleFileLocation.getDirectory(),
                getFileName(locale)
        );
    }

    /**
     * Reads translations from the specified file using the specified stream. The map is a structured
     * map of translations.
     */
    private Map<String, Object> readTranslations(File file, InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw WorkspaceException.onFileReading(file, e);
        }
    }

    /**
     * Inlines all values from the specified structured translations.
     *
     * @see #toStructuredMap(List)
     */
    private Flux<Pair<String, String>> inlineValues(Map<String, Object> translations) {
        return inlineValues(translations, "");
    }

    /**
     * Inlines all values from the specified structured translations knowing the specified current key of the parent.
     * <p>
     * For instance:
     * <ul>
     *     <li>the current parent key is: <tt>workspace.message</tt>,</li>
     *     <li>the structured translations are: <tt>{"error": "my message"}</tt></li>
     * </ul>
     * <p>
     * The result will be: <tt>workspace.message.error = "my message"</tt>
     */
    @SuppressWarnings("unchecked")
    private Flux<Pair<String, String>> inlineValues(Map<String, Object> translations, String currentParentKey) {
        return Flux.fromStream(translations.entrySet().stream())
                .flatMap(entry -> {
                    if (entry.getValue() instanceof Map) {
                        return inlineValues((Map<String, Object>) entry.getValue(), createKey(currentParentKey, entry.getKey()));
                    } else if (entry.getValue() instanceof String) {
                        return Flux.just(Pair.of(createKey(currentParentKey, entry.getKey()), mapToNullIfEmpty((String) entry.getValue())));
                    } else {
                        return Flux.empty();
                    }
                });
    }

    /**
     * Creates a key concatenating the parent key with the sub-key.
     */
    private String createKey(String parentKey, String subKey) {
        return isEmpty(parentKey) ? subKey : parentKey + "." + subKey;
    }

    /**
     * Writes the specified translations in the specified file using the output-stream.
     */
    private Mono<Void> writeTranslations(Flux<Pair<String, String>> translations, File file, File outputStream) {
        return translations
                .collectList()
                .map(this::toStructuredMap)
                .doOnNext(structuredMap -> {
                    try {
                        objectMapper.writeValue(outputStream, structuredMap);
                    } catch (Exception e) {
                        throw WorkspaceException.onFileWriting(file, e);
                    }
                })
                .then();
    }

    /**
     * Returns a structured map from inlined properties.
     *
     * @see #inlineValues(Map)
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> toStructuredMap(List<Pair<String, String>> translations) {
        final Map<String, Object> map = new LinkedHashMap<>();

        translations.forEach(
                translation -> {
                    Map<String, Object> currentMap = map;

                    final String[] parts = translation.getKey().split("\\.");
                    for (int i = 0; i < parts.length; i++) {
                        if (i == (parts.length - 1)) {
                            currentMap.put(parts[i], translation.getValue());
                        } else {
                            currentMap.putIfAbsent(parts[i], new LinkedHashMap<>());
                            currentMap = (Map<String, Object>) currentMap.get(parts[i]);
                        }
                    }
                }
        );

        return map;
    }

    /**
     * {@link DefaultPrettyPrinter Pretty printer} that formats JSON in the same way as the default formatting of IntelliJ.
     */
    private static final class CustomPrettyPrinter extends DefaultPrettyPrinter {

        public CustomPrettyPrinter() {
        }

        public CustomPrettyPrinter(CustomPrettyPrinter base) {
            super(base);
        }

        @Override
        public DefaultPrettyPrinter createInstance() {
            return new CustomPrettyPrinter(this);
        }

        @Override
        public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
            g.writeRaw(": ");
        }
    }
}
