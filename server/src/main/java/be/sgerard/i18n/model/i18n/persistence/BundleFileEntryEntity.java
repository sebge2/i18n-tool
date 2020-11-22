package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.i18n.file.ScannedBundleFileEntry;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.UUID;

/**
 * File composing a {@link BundleFileEntity bundle file}.
 *
 * @author Sebastien Gerard
 */
@Getter
@Setter
@Accessors(chain = true)
public class BundleFileEntryEntity {

    /**
     * The unique id of this bundle.
     */
    @Id
    private String id;

    /**
     * The {@link TranslationLocaleEntity locale} of this file.
     */
    @NotNull
    private String locale;

    /**
     * The file part of the bundle file.
     */
    @NotNull
    private String file;

    @PersistenceConstructor
    BundleFileEntryEntity() {
    }

    public BundleFileEntryEntity(String locale, String file) {
        this.id = UUID.randomUUID().toString();
        this.locale = locale;
        this.file = file;
    }

    public BundleFileEntryEntity(ScannedBundleFileEntry entry) {
        this(entry.getLocale().getId(), entry.getFile().toString());
    }

    /**
     * Returns the file part of the bundle file.
     */
    public File getJavaFile() {
        return new File(getFile());
    }

}
