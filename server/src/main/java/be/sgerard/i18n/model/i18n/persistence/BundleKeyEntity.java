package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Translations of a certain key part of translation bundle.
 *
 * @author Sebastien Gerard
 */
@Document("bundle_key")
@Getter
@Setter
public class BundleKeyEntity {

    /**
     * The unique translation key id.
     */
    @Id
    private String id;

    /**
     * The associated {@link WorkspaceEntity workspace}.
     */
    @NotNull
    private String workspace;

    /**
     * The associated {@link BundleFileEntity bundle file}.
     */
    @NotNull
    private String bundleFile;

    /**
     * The associated translation key.
     */
    @NotNull
    private String key;

    /**
     * Key used to sort all translation entities. It's composed of:
     * <ol>
     *     <li>the workspace,</li>
     *     <li>the bundle file,</li>
     *     <li>the bundle key and</li>
     *     <li>the locale id.</li>
     * </ol>
     */
    @NotNull
    @Indexed
    private String sortingKey;

    /**
     * All {@link BundleKeyTranslationEntity translations} mapped by their locale ids.
     */
    @NotNull
    @Singular
    private Map<String, BundleKeyTranslationEntity> translations = new HashMap<>();

    @PersistenceConstructor
    BundleKeyEntity() {
    }

    public BundleKeyEntity(String workspace,
                           String bundleFile,
                           String key,
                           String locale) {
        this.id = UUID.randomUUID().toString();
        this.workspace = workspace;
        this.bundleFile = bundleFile;
        this.key = key;
        this.sortingKey = String.format("%s%s%s%s", workspace, bundleFile, key, locale);
    }
}
