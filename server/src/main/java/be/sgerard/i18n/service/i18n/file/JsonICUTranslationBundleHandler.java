package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKeyDto;
import be.sgerard.i18n.service.git.RepositoryAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.WrappedIOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static be.sgerard.i18n.service.i18n.file.TranslationFileUtils.mapToNullIfEmpty;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.*;
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

    @Autowired
    public JsonICUTranslationBundleHandler(AppProperties appProperties, ObjectMapper objectMapper) {
        this.pathsToScan = appProperties.getJsonIcuTranslationBundleDirsAsList();
        this.objectMapper = objectMapper;
        this.antPathMatcher = new AntPathMatcher();
    }

    public JsonICUTranslationBundleHandler(AppProperties appProperties) {
        this(appProperties, new ObjectMapper());
    }

    @Override
    public boolean support(ScannedBundleFileDto bundleFile) {
        return bundleFile.getType() == BundleType.JSON_ICU;
    }

    @Override
    public boolean continueScanning(File directory) {
        return true;
    }

    @Override
    public Stream<ScannedBundleFileDto> scanBundles(File directory, RepositoryAPI repositoryAPI) throws IOException {
        if (pathsToScan.stream().noneMatch(dirPathPattern -> antPathMatcher.match(dirPathPattern, directory.toPath().toString()))) {
            return Stream.empty();
        }

        return repositoryAPI.listNormalFiles(directory)
                .map(
                        file -> {
                            final Matcher matcher = BUNDLE_PATTERN.matcher(file.getName());

                            if (matcher.matches()) {
                                return new ScannedBundleFileDto(
                                        directory.getName(),
                                        BundleType.JSON_ICU,
                                        directory,
                                        singletonList(Locale.forLanguageTag(matcher.group(1))),
                                        singletonList(file)
                                );
                            } else {
                                return null;
                            }
                        }
                )
                .filter(Objects::nonNull)
                .collect(groupingBy(ScannedBundleFileDto::getName, reducing(null, ScannedBundleFileDto::merge)))
                .values()
                .stream();
    }

    @Override
    public Collection<ScannedBundleFileKeyDto> scanKeys(ScannedBundleFileDto bundleFile, RepositoryAPI repositoryAPI) throws IOException {
        try {
            return bundleFile.getFiles().stream()
                    .flatMap(
                            file -> {
                                try {
                                    return inlineValues(readValues(repositoryAPI, file))
                                            .entrySet().stream()
                                            .map(entry -> new ScannedBundleFileKeyDto(entry.getKey(), singletonMap(getLocale(file), mapToNullIfEmpty(entry.getValue()))));
                                } catch (IOException e) {
                                    throw new WrappedIOException(e);
                                }
                            }
                    )
                    .collect(groupingBy(ScannedBundleFileKeyDto::getKey, LinkedHashMap::new, reducing(null, ScannedBundleFileKeyDto::merge)))
                    .values();
        } catch (WrappedIOException e) {
            throw e.getCause();
        }
    }

    @Override
    public void updateBundle(ScannedBundleFileDto bundleFile, Collection<ScannedBundleFileKeyDto> keys, RepositoryAPI repositoryAPI) throws IOException {
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readValues(RepositoryAPI repositoryAPI, File file) throws IOException {
        return objectMapper.readValue(repositoryAPI.openInputStream(file), LinkedHashMap.class);
    }

    private Map<String, String> inlineValues(Map<String, Object> originalValues) {
        return inlineValues(originalValues, "").collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
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
}
