package be.sgerard.i18n.model.dictionary;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.Optional;

/**
 * Request asking to search for dictionary entries.
 *
 * @author Sebastien Gerard
 */
@Getter
@Builder(builderClassName = "Builder")
public class DictionaryEntrySearchRequest {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Restriction to apply on the translation.
     */
    private final TranslationRestriction translation;

    /**
     * Configuration of the sort to apply.
     */
    private final Sort sort;

    /**
     * @see #translation
     */
    public Optional<TranslationRestriction> getText() {
        return Optional.ofNullable(translation);
    }

    /**
     * @see #sort
     */
    public Optional<Sort> getSort() {
        return Optional.ofNullable(sort);
    }

    /**
     * Restriction to apply on the translation.
     */
    @Value
    @SuppressWarnings("RedundantModifiersValueLombok")
    public static final class TranslationRestriction {

        /**
         * The translation must contain the specified text (case-insensitive).
         */
        private final String text;

        /**
         * The ID of the locale in which the specified text is written.
         */
        private final String localeId;
    }

    /**
     * Configuration of the sort to apply.
     */
    @Value
    @SuppressWarnings("RedundantModifiersValueLombok")
    public static final class Sort {

        /**
         * Flag indicating whether the sorting is ascending (or descending).
         */
        private final boolean ascending;

        /**
         * The ID of the locale of translations to sort on.
         */
        private final String localeId;

    }
}
