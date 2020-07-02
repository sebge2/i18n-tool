package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.i18n.file.ScannedBundleFileEntry;
import lombok.Getter;
import lombok.Setter;
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
    private String locale; // TODO

    /**
     * The file part of the bundle file.
     */
    @NotNull
    private String file;

    @PersistenceConstructor
    BundleFileEntryEntity() {
    }

    public BundleFileEntryEntity(ScannedBundleFileEntry entry) {
        this.id = UUID.randomUUID().toString();
        this.locale = entry.getLocale().getId();
        this.file = entry.getFile().toString();
    }

    /**
     * Returns the file part of the bundle file.
     */
    public File getJavaFile() {
        return new File(getFile());
    }

}
