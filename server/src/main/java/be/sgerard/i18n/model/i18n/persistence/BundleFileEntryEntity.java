package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.i18n.file.ScannedBundleFileEntry;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.validation.constraints.NotNull;
import java.io.File;

/**
 * File composing a {@link BundleFileEntity bundle file}.
 *
 * @author Sebastien Gerard
 */
public class BundleFileEntryEntity {

    @NotNull
    private String locale; // TODO

    @NotNull
    private String file;

    @PersistenceConstructor
    BundleFileEntryEntity() {
    }

    public BundleFileEntryEntity(TranslationLocaleEntity locale, String file) {
        this.locale = locale.getId();
        this.file = file;
    }

    public BundleFileEntryEntity(ScannedBundleFileEntry entry) {
        this(entry.getLocale(), entry.getFile().toString());
    }

    /**
     * Returns the {@link TranslationLocaleEntity locale} of this file.
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the {@link TranslationLocaleEntity locale} of this file.
     */
    public BundleFileEntryEntity setLocale(String locale) {
        this.locale = locale;
        return this;
    }

    /**
     * Returns the file part of the bundle file.
     */
    public String getFile() {
        return file;
    }

    /**
     * Returns the file part of the bundle file.
     */
    public File getJavaFile() {
        return new File(getFile());
    }

    /**
     * Sets the file part of the bundle file.
     */
    public BundleFileEntryEntity setFile(String file) {
        this.file = file;
        return this;
    }
}
