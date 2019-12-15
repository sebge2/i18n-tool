package be.sgerard.i18n.model.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * @author Sebastien Gerard
 */
@ReadingConverter
public class StringToPatternConverter implements Converter<String, Pattern> {

    @Override
    public Pattern convert(String source) {
        return Pattern.compile(source);
    }
}

