package be.sgerard.i18n.model.i18n.file;

import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;

import java.io.File;

/**
 * File part of a {@link ScannedBundleFile bundle file}.
 *
 * @author Sebastien Gerard
 */
public class ScannedBundleFileEntry {

    private final TranslationLocaleEntity locale;
    private final File file;

    public ScannedBundleFileEntry(TranslationLocaleEntity locale, File file) {
        this.locale = locale;
        this.file = file;
    }

    /**
     * Returns the {@link TranslationLocaleEntity locale} associated to the file.
     */
    public TranslationLocaleEntity getLocale() {
        return locale;
    }

    /**
     * Returns the translation file.
     */
    public File getFile() {
        return file;
    }
}
