package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
public class TranslationLocaleEntityAsserter {

    public static TranslationLocaleEntityAsserter newAssertion() {
        return new TranslationLocaleEntityAsserter();
    }

    public TranslationLocaleEntityAsserter expectEquals(TranslationLocaleEntity actual, TranslationLocaleEntity expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getLanguage()).isEqualTo(expected.getLanguage());
        assertThat(actual.getRegion()).isEqualTo(expected.getRegion());
        assertThat(actual.getVariants()).isEqualTo(expected.getVariants());
        assertThat(actual.getDisplayName()).isEqualTo(expected.getDisplayName());
        assertThat(actual.getIcon()).isEqualTo(expected.getIcon());

        return this;
    }

}
