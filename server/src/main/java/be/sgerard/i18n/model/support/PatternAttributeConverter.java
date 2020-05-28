package be.sgerard.i18n.model.support;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.regex.Pattern;

/**
 * Converts at the JPA level, a {@link Pattern} from and to a String.
 *
 * @author Sebastien Gerard
 */
@Converter(autoApply = true)
public class PatternAttributeConverter implements AttributeConverter<Pattern, String> {

    @Override
    public String convertToDatabaseColumn(Pattern attribute) {
        return attribute == null
                ? null
                : attribute.pattern();
    }

    @Override
    public Pattern convertToEntityAttribute(String dbData) {
        return dbData == null
                ? null
                : Pattern.compile(dbData);
    }
}
