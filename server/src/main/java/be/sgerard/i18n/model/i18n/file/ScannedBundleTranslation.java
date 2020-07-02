package be.sgerard.i18n.model.i18n.file;

import lombok.Getter;

import java.util.Optional;

/**
 * Translation found in a bundle file entry.
 *
 * @author Sebastien Gerard
 */
@Getter
public class ScannedBundleTranslation {

    /**
     * The {@link ScannedBundleFileEntry file entry} containing this translation.
     */
    private final ScannedBundleFileEntry fileEntry;

    /**
     * The key identifying the translation.
     */
    private final String key;

    /**
     * The translation.
     */
    private final String value;

    public ScannedBundleTranslation(ScannedBundleFileEntry fileEntry, String key, String value) {
        this.fileEntry = fileEntry;
        this.key = key;
        this.value = value;
    }

    /**
     * @see #value
     */
    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }
}
