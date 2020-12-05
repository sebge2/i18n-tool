package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
public class BundleKeyEntityAsserter {

    public static BundleKeyEntityAsserter newAssertion() {
        return new BundleKeyEntityAsserter();
    }

    public BundleKeyEntityAsserter expectEquals(BundleKeyEntity actual, BundleKeyEntity expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getWorkspace()).isEqualTo(expected.getWorkspace());
        assertThat(actual.getBundleFile()).isEqualTo(expected.getBundleFile());
        assertThat(actual.getKey()).isEqualTo(expected.getKey());
        assertThat(actual.getSortingKey()).isEqualTo(expected.getSortingKey());

        assertThat(expected.getTranslations()).hasSameSizeAs(actual.getTranslations());
        for (BundleKeyTranslationEntity actualTranslation : actual.getTranslations().values()) {
            assertThat(expected.getTranslations()).containsKey(actualTranslation.getLocale());

            doExpectEquals(actualTranslation, expected.getTranslationOrDie(actualTranslation.getLocale()));
        }

        return this;
    }

    private void doExpectEquals(BundleKeyTranslationEntity actual, BundleKeyTranslationEntity expected) {
        assertThat(actual.getLocale()).isEqualTo(expected.getLocale());
        assertThat(actual.getIndex()).isEqualTo(expected.getIndex());
        assertThat(actual.getValue()).isEqualTo(expected.getValue());
        assertThat(actual.getOriginalValue()).isEqualTo(expected.getOriginalValue());

        if (actual.getModification().isPresent()) {
            assertThat(expected.getModification()).isNotEmpty();

            assertThat(actual.getModification().get().getUpdatedValue()).isEqualTo(expected.getModification().get().getUpdatedValue());
            assertThat(actual.getModification().get().getLastEditor()).isEqualTo(expected.getModification().get().getLastEditor());
        } else {
            assertThat(expected.getModification()).isEmpty();
        }
    }
}
