package be.sgerard.i18n.model.support;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Locale;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * {@link Converter} from {@link Map} to {@link LocalizedString}.
 *
 * @author Sebastien Gerard
 */
@ReadingConverter
public class MapToLocalizedStringConverter implements Converter<Map<String, String>, LocalizedString> {

    @Override
    public LocalizedString convert(Map<String, String> map) {
        return new LocalizedString(
                map.entrySet().stream()
                        .collect(toMap(
                                entry -> Locale.forLanguageTag(entry.getKey()),
                                Map.Entry::getValue
                        ))
        );
    }
}
