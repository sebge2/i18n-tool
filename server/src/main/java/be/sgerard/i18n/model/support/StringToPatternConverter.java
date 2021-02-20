package be.sgerard.i18n.model.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.regex.Pattern;

/**
 * {@link Converter} from {@link String} to {@link Pattern}.
 *
 * @author Sebastien Gerard
 */
@ReadingConverter
public class StringToPatternConverter implements Converter<String, Pattern> {

    @Override
    public Pattern convert(String source) {
        return Pattern.compile(source);
    }
}

