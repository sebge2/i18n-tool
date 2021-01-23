package be.sgerard.i18n.model.support;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Map;

import static java.util.stream.Collectors.toMap;


/**
 * {@link Converter} from {@link LocalizedString} to {@link Map}.
 *
 * @author Sebastien Gerard
 */
@WritingConverter
public class LocalizedStringToMapConverter implements Converter<LocalizedString, Map<String, String>> {

    @Override
    public Map<String, String> convert(LocalizedString localizedString) {
        return localizedString.getTranslations().entrySet().stream()
                .collect(toMap(
                        entry -> entry.getKey().toLanguageTag(),
                        Map.Entry::getValue
                ));
    }
}
