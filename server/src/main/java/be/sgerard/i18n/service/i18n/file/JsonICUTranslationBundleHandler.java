package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKeyDto;
import be.sgerard.i18n.service.i18n.TranslationRepositoryReadApi;
import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.sgerard.i18n.service.i18n.file.TranslationFileUtils.mapToNullIfEmpty;
import static java.util.Collections.emptyList;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Sebastien Gerard
 */
@Component
public class JsonICUTranslationBundleHandler implements TranslationBundleHandler {

    private static final Pattern BUNDLE_PATTERN = Pattern.compile("^(.*)\\.json$");

    private final List<String> pathsToScan;
    private final AntPathMatcher antPathMatcher;
    private final ObjectMapper objectMapper;

    public JsonICUTranslationBundleHandler(AppProperties appProperties, ObjectMapper objectMapper) {
        this.pathsToScan = appProperties.getJsonIcuTranslationBundleDirsAsList();
        this.objectMapper = objectMapper;
        this.antPathMatcher = new AntPathMatcher();
    }

    @Autowired
    public JsonICUTranslationBundleHandler(AppProperties appProperties) {
        this(appProperties, new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT));
    }

    @Override
    public boolean support(ScannedBundleFileDto bundleFile) {
        return bundleFile.getType() == BundleType.JSON;
    }

    @Override
    public boolean continueScanning(File directory) {
        return true;
    }

    @Override
    public Stream<ScannedBundleFileDto> scanBundles(File directory, TranslationRepositoryReadApi repositoryAPI) {
        return null;
//        if (pathsToScan.stream().noneMatch(dirPathPattern -> antPathMatcher.match(dirPathPattern, directory.toPath().toString()))) {
//            return Stream.empty();
//        }
//
//        return repositoryAPI.listNormalFiles(directory)
//                .map(
//                        file -> {
//                            final Matcher matcher = BUNDLE_PATTERN.matcher(file.getName());
//
//                            if (matcher.matches()) {
//                                return new ScannedBundleFileDto(
//                                        directory.getName(),
//                                        BundleType.JSON_ICU,
//                                        directory,
//                                        singletonList(Locale.forLanguageTag(matcher.group(1))),
//                                        singletonList(file)
//                                );
//                            } else {
//                                return null;
//                            }
//                        }
//                )
//                .filter(Objects::nonNull)
//                .collect(groupingBy(ScannedBundleFileDto::getName, reducing(null, ScannedBundleFileDto::merge)))
//                .values()
//                .stream();
    }

    @Override
    public List<ScannedBundleFileKeyDto> scanKeys(ScannedBundleFileDto bundleFile, TranslationRepositoryReadApi repositoryAPI) {
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
    public void updateBundle(ScannedBundleFileDto bundleFile, List<ScannedBundleFileKeyDto> keys, TranslationRepositoryWriteApi repositoryAPI) {
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
    private Map<String, Object> toMap(List<ScannedBundleFileKeyDto> keys, Locale locale) {
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
