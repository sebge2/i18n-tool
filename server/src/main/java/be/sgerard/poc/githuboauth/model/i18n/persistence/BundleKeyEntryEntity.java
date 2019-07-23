package be.sgerard.poc.githuboauth.model.i18n.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Sebastien Gerard
 */
@NamedEntityGraph(
        name = BundleKeyEntryEntity.GRAPH_FETCH_ENTRIES_TO_WORKSPACE,
        attributeNodes = {
                @NamedAttributeNode(value = "bundleKey", subgraph = "bundleKey-subgraph"),
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "bundleKey-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "bundleFile", subgraph = "workspace-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "workspace-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "workspace")
                        }
                )
        }
)
@Entity(name = "translation_bundle_key_entry")
@Table(
        indexes = {@Index(columnList = "locale")},
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"bundle_key", "locale"})
        }
)
public class BundleKeyEntryEntity {

    public static final String GRAPH_FETCH_ENTRIES_TO_WORKSPACE = "fetch-entry-with-bundles";

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "bundle_key")
    private BundleKeyEntity bundleKey;

    @NotNull
    @Column(nullable = false)
    private String locale;

    @Column
    private String originalValue;

    @Column
    private String updatedValue;

    @Column
    private String lastEditor;

    @Version
    private int version;

    BundleKeyEntryEntity() {
    }

    public BundleKeyEntryEntity(BundleKeyEntity bundleKey,
                                String locale,
                                String originalValue) {
        this.id = UUID.randomUUID().toString();

        this.bundleKey = bundleKey;
        this.bundleKey.addEntry(this);

        this.locale = locale;
        this.originalValue = originalValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BundleKeyEntity getBundleKey() {
        return bundleKey;
    }

    public void setBundleKey(BundleKeyEntity bundleKey) {
        this.bundleKey = bundleKey;
    }

    public String getLocale() {
        return locale;
    }

    public Locale getJavaLocale(){
        return Locale.forLanguageTag(getLocale());
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Optional<String> getOriginalValue() {
        return Optional.ofNullable(originalValue);
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    public Optional<String> getUpdatedValue() {
        return Optional.ofNullable(updatedValue);
    }

    public void setUpdatedValue(String updatedValue) {
        this.updatedValue = updatedValue;
    }

    public Optional<String> getValue() {
        return getUpdatedValue()
                .or(this::getOriginalValue);
    }

    public Optional<String> getLastEditor() {
        return Optional.ofNullable(lastEditor);
    }

    public void setLastEditor(String lastEditor) {
        this.lastEditor = lastEditor;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
