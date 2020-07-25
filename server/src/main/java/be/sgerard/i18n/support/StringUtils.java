package be.sgerard.i18n.support;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Bunch of utility methods for Strings.
 *
 * @author Sebastien Gerard
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static String isEmptyString(String value) {
        return !isEmpty(value) && !isEmpty(value.trim()) ? value : null;
    }
}
