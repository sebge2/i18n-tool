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

    /**
     * Returns whether the specified string contains useful characters.
     */
    public static boolean isEmptyString(String value) {
        return !isEmpty(value) && !isEmpty(value.trim());
    }
}
