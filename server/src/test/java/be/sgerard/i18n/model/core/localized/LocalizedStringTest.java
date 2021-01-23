package be.sgerard.i18n.model.core.localized;

import be.sgerard.i18n.model.ToolLocale;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
public class LocalizedStringTest {

    @Test
    public void format(){
        final LocalizedString initial = new LocalizedString(Locale.ENGLISH, "First parameter is: {0}");
        final LocalizedString actual = initial.formatWithDefault("my value");

        assertThat(actual.getTranslation(ToolLocale.ENGLISH)).contains("First parameter is: my value");
    }

    @Test
    public void formatParameterLocalized(){
        final LocalizedString initial = new LocalizedString(Locale.ENGLISH, "First parameter is: {0}");
        final LocalizedString actual = initial.formatWithDefault(new LocalizedString(Locale.ENGLISH, "my value"));

        assertThat(actual.getTranslation(ToolLocale.ENGLISH)).contains("First parameter is: my value");
    }

}
