package be.sgerard.i18n.model.core.localized;

import be.sgerard.i18n.model.ToolLocale;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
public class LocalizedStringResourceBundleTest {

    @Test
    public void load(){
        final LocalizedStringResourceBundle actual = new LocalizedStringResourceBundle("be/sgerard/i18n/service/i18n/file/test_translations");

        assertThat(actual.toLocalizedString("first-root.first").getTranslation(ToolLocale.ENGLISH)).contains("first value");
    }

}
