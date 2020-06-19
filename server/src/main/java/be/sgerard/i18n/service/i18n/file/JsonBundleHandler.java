package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.BundleWalkContext;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKey;
import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import be.sgerard.i18n.service.workspace.WorkspaceException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static be.sgerard.i18n.service.i18n.file.TranslationFileUtils.mapToNullIfEmpty;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * {@link BundleHandler Bundle handler} for JSON files.
 *
 * @author Sebastien Gerard
 */
@Component
public class JsonBundleHandler implements BundleHandler {

    /**
     * Pattern for a translation bundle file. It's composed of the language (2 letters in lower case) and then
     * the region (2 letters in upper-case)
     */
    private static final Pattern BUNDLE_PATTERN = Pattern.compile("^([a-z]{2}(_[A-Z]{2})?)\\.json$");

    private final ObjectMapper objectMapper;

    public JsonBundleHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public JsonBundleHandler() {
        this(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT));
    }

    @Override
    public boolean support(BundleType bundleType) {
        return bundleType == BundleType.JSON_ICU;
    }

    @Override
    public boolean continueScanning(File directory, BundleWalkContext context) {
        return context.canWalkTrough(BundleType.JSON_ICU, directory.toPath());
    }

    @Override
    public Flux<ScannedBundleFile> scanBundles(File directory, BundleWalkContext context) {
        return context
                .getApi()
                .listNormalFiles(directory)
                .flatMap(
                        file -> {
                            final Matcher matcher = BUNDLE_PATTERN.matcher(file.getName());

                            if (matcher.matches()) {
                                return Mono.just(
                                        new ScannedBundleFile(
                                                directory.getName(),
                                                BundleType.JSON_ICU,
                                                directory,
                                                singletonList(Locale.forLanguageTag(matcher.group(1))),
                                                singletonList(file)
                                        )
                                );
                            } else {
                                return Mono.empty();
                            }
                        }
                )
                .filter(bundleFile -> context.getLocales().containsAll(bundleFile.getLocales()))
                .groupBy(ScannedBundleFile::getName)
                .flatMap(group -> group.reduce(ScannedBundleFile::merge));
    }

    @Override
    public Flux<ScannedBundleFileKey> scanKeys(ScannedBundleFile bundleFile, BundleWalkContext context) {
        return Flux
                .fromStream(bundleFile.getFiles().stream())
                .flatMap(file ->
                        context
                                .getApi()
                                .openInputStream(file)
                                .map(inputStream -> readTranslations(file, inputStream))
                                .flatMapMany(translations ->
                                        inlineValues(translations)
                                                .map(entry -> new ScannedBundleFileKey(entry.getKey(), singletonMap(getLocale(file), mapToNullIfEmpty(entry.getValue()))))
                                )
                )
                .groupBy(ScannedBundleFileKey::getKey)
                .flatMap(group -> group.reduce(ScannedBundleFileKey::merge));
    }

    @Override
    public Mono<Void> updateBundle(ScannedBundleFile bundleFile, List<ScannedBundleFileKey> keys, TranslationRepositoryWriteApi repositoryAPI) {
        return Flux
                .fromIterable(bundleFile.getFiles())
                .filter(file -> BUNDLE_PATTERN.matcher(file.getName()).matches())
                .flatMap(file ->
                        repositoryAPI
                                .openAsTemp(file)
                                .doOnNext(outputStream -> writeTranslations(keys, file, outputStream))
                )
                .then();
    }

    /**
     * Returns the locale based on the file name.
     *
     * @see #BUNDLE_PATTERN
     */
    private Locale getLocale(File file) {
        final Matcher matcher = BUNDLE_PATTERN.matcher(file.getName());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("The file [" + file + "] is not a resource bundle file.");
        }

        return Locale.forLanguageTag(matcher.group(1));
    }

    /**
     * Reads translations from the specified file using the specified stream. The map is a structured
     * map of translations.
     */
    private Map<String, Object> readTranslations(File file, InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw WorkspaceException.onFileReading(file, e);
        }
    }

    /**
     * Inlines all values from the specified structured translations.
     *
     * @see #toStructuredMap(List, Locale)
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
     * Writes the specified keys in the specified file using the output-stream.
     */
    private void writeTranslations(List<ScannedBundleFileKey> keys, File file, File outputStream) {
        try {
            objectMapper.writeValue(outputStream, toStructuredMap(keys, getLocale(file)));
        } catch (IOException e) {
            throw WorkspaceException.onFileWriting(file, e);
        }
    }

    /**
     * Returns a structured map from inlined properties.
     *
     * @see #inlineValues(Map)
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> toStructuredMap(List<ScannedBundleFileKey> keys, Locale locale) {
        final Map<String, Object> map = new LinkedHashMap<>();

        keys.forEach(
                key -> {
                    Map<String, Object> currentMap = map;

                    final String[] parts = key.getKey().split("\\.");
                    for (int i = 0; i < parts.length; i++) {
                        if (i == (parts.length - 1)) {
                            currentMap.put(parts[i], key.getTranslations().get(locale));
                        } else {
                            currentMap.putIfAbsent(parts[i], new LinkedHashMap<>());
                            currentMap = (Map<String, Object>) currentMap.get(parts[i]);
                        }
                    }
                }
        );

        return map;
    }
}
