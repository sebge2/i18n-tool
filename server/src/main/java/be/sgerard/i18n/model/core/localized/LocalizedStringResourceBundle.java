package be.sgerard.i18n.model.core.localized;

import be.sgerard.i18n.support.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * Resource bundle of localized strings. These resources are defined in properties files.
 *
 * @author Sebastien Gerard
 */
public class LocalizedStringResourceBundle {

    private static final UTF8Control UTF8_CONTROL = new UTF8Control();
    private static final Pattern PROPERTIES_FILE_NAME_PATTERN = Pattern.compile(".*_([a-zA-Z]{2}).properties");

    private static final Map<String, List<Locale>> LOCALES_PER_BUNDLE_CACHE = new HashMap<>();

    private final String bundlePath;
    private final Map<Locale, ResourceBundle> bundleEntries;

    public LocalizedStringResourceBundle(String bundlePath) {
        this.bundlePath = bundlePath;
        this.bundleEntries = loadBundleEntries(bundlePath);
    }

    /**
     * Returns the path to the bundle. Ex: "i18n/translations". The bundle path does not contain file extension.
     */
    public String getBundlePath() {
        return bundlePath;
    }

    /**
     * Returns the {@link LocalizedString localized string} for the specified property.
     */
    public LocalizedString toLocalizedString(String property) {
        return new LocalizedString(
                bundleEntries.entrySet().stream()
                        .filter(entry -> entry.getValue().containsKey(property))
                        .filter(entry -> StringUtils.isNotEmptyString(entry.getValue().getString(property)))
                        .collect(toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().getString(property)
                        ))
        );
    }

    /**
     * Returns all the available {@link ResourceBundle resource bundles} available for the specified bundle.
     */
    private static Map<Locale, ResourceBundle> loadBundleEntries(String bundlePath) {
        return LOCALES_PER_BUNDLE_CACHE
                .computeIfAbsent(bundlePath, LocalizedStringResourceBundle::loadLocalesForBundle).stream()
                .collect(toMap(
                        Function.identity(),
                        locale -> ResourceBundle.getBundle(bundlePath, locale, UTF8_CONTROL)
                ));
    }

    /**
     * Returns all {@link Locale locales} available for the given bundle.
     * <p>
     * Example: the following bundle file entries are associated to the bundle path "i18n.translation":
     * <ul>
     *     <li> "i18n/translation.properties" </li>
     *     <li> "i18n/translation_en.properties" </li>
     *     <li> "i18n/translation_fr.properties" </li>
     *     <li> "i18n/translation_nl.properties" </li>
     * </ul>
     * <p>
     * => will return [Locale("en"), Locale("fr"), Locale("nl")]
     */
    private static List<Locale> loadLocalesForBundle(String bundlePath) {
        try {
            return Arrays.stream(new PathMatchingResourcePatternResolver().getResources(bundlePath + "*.properties"))
                    .map(LocalizedStringResourceBundle::loadLocaleForBundleEntry)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Error while loading the bundle [%s].", bundlePath), e);
        }
    }

    /**
     * Returns the {@link Locale locale} for a given resource.
     * <p>
     * Example: "i18n/translation_en.properties" will returns Locale("en")
     *
     * @see Locale#forLanguageTag(java.lang.String)
     */
    private static Optional<Locale> loadLocaleForBundleEntry(Resource resource) {
        return Optional
                .ofNullable(resource.getFilename())
                .map(PROPERTIES_FILE_NAME_PATTERN::matcher)
                .filter(matcher -> matcher.matches() && (matcher.groupCount() > 0))
                .map(matcher -> Locale.forLanguageTag(matcher.group(1)));
    }

    /**
     * {@link ResourceBundle.Control Resource bundle control} supporting UTF-8.
     *
     * @see <a href="https://stackoverflow.com/questions/4659929/how-to-use-utf-8-in-resource-properties-with-resourcebundle">Based on thread.</a>
     */
    public static final class UTF8Control extends ResourceBundle.Control {

        @Override
        public ResourceBundle newBundle(String baseName,
                                        Locale locale,
                                        String format,
                                        ClassLoader loader,
                                        boolean reload) throws IOException {
            // The below is a copy of the default implementation.
            final String bundleName = toBundleName(baseName, locale);
            final String resourceName = toResourceName(bundleName, "properties");

            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }

            if (stream != null) {
                try {
                    // Only this line is changed to make it to read properties files as UTF-8.
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
                } finally {
                    stream.close();
                }
            }

            return bundle;
        }
    }

}
