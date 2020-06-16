package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.BundleWalkContext;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKey;
import be.sgerard.i18n.service.i18n.TranslationRepositoryReadApi;
import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.sgerard.i18n.service.i18n.file.TranslationFileUtils.mapToNullIfEmpty;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * {@link BundleHandler Bundle handler} for JSON files.
 *
 * @author Sebastien Gerard
 */
@Component
public class JsonBundleHandler implements BundleHandler {

    private static final Pattern BUNDLE_PATTERN = Pattern.compile("^(.*)\\.json$");

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
        return context.isIncluded(BundleType.JSON_ICU, directory.toPath());
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
    public List<ScannedBundleFileKey> scanKeys(ScannedBundleFile bundleFile, TranslationRepositoryReadApi api) {
        return emptyList();
//            return new ArrayList<>(
//                    bundleFile.getFiles().stream()
//                            .flatMap(
//                                    file -> {
//                                        try {
//                                            return inlineValues(readValues(repositoryAPI, file))
//                                                    .entrySet().stream()
//                                                    .map(entry -> new ScannedBundleFileKeyDto(entry.getKey(), singletonMap(getLocale(file), mapToNullIfEmpty(entry.getValue()))));
//                                        } catch (IOException e) {
//                                            throw new WrappedIOException(e);
//                                        }
//                                    }
//                            )
//                            .collect(groupingBy(ScannedBundleFileKeyDto::getKey, LinkedHashMap::new, reducing(null, ScannedBundleFileKeyDto::merge)))
//                            .values()
//            );
    }

    @Override
    public void updateBundle(ScannedBundleFile bundleFile, List<ScannedBundleFileKey> keys, TranslationRepositoryWriteApi repositoryAPI) {
        for (File file : bundleFile.getFiles()) {
            final Matcher matcher = BUNDLE_PATTERN.matcher(file.getName());

            if (!matcher.matches()) {
                continue;
            }

            final Locale locale = getLocale(file);

//            objectMapper.writeValue(repositoryAPI.openAsTemp(file), toMap(keys, locale));
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readValues(GitRepositoryApi repositoryAPI, File file) throws IOException {
        return objectMapper.readValue(repositoryAPI.openInputStream(file), LinkedHashMap.class);
    }

    private Map<String, String> inlineValues(Map<String, Object> originalValues) {
        return inlineValues(originalValues, "").collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (first, second) -> first, LinkedHashMap::new));
    }

    @SuppressWarnings("unchecked")
    private Stream<Map.Entry<String, String>> inlineValues(Map<String, Object> originalValues, String currentParentKey) {
        return originalValues.entrySet().stream()
                .flatMap(entry -> {
                    if (entry.getValue() instanceof Map) {
                        return inlineValues((Map<String, Object>) entry.getValue(), createKey(currentParentKey, entry.getKey()));
                    } else if (entry.getValue() instanceof String) {
                        return Stream.of(new AbstractMap.SimpleEntry<>(createKey(currentParentKey, entry.getKey()), mapToNullIfEmpty((String) entry.getValue())));
                    } else {
                        return Stream.empty();
                    }
                });
    }

    private String createKey(String currentParentKey, String key) {
        return isEmpty(currentParentKey) ? key : currentParentKey + "." + key;
    }

    private Locale getLocale(File file) {
        final Matcher matcher = BUNDLE_PATTERN.matcher(file.getName());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("The file [" + file + "] is not a resource bundle file.");
        }

        return Locale.forLanguageTag(matcher.group(1));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(List<ScannedBundleFileKey> keys, Locale locale) {
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
