package be.sgerard.i18n.model.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.regex.Pattern;

/**
 * @author Sebastien Gerard
 */
@WritingConverter
public class PatternToStringConverter implements Converter<Pattern, String> {

    @Override
    public String convert(Pattern source) {
        return source.pattern();
    }
}

