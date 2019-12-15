package be.sgerard.i18n.model.i18n.persistence;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @author Sebastien Gerard
 */
@Entity(name = "translation_locale")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"language", "region"})
        }
)
public class TranslationLocaleEntity {

    @Id
    private String id;

    @NotNull
    @Column(nullable = false)
    private String language;

    @Column
    private String region;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "translation_locale_variant", joinColumns = @JoinColumn(name = "locale_id"))
    @Column
    private Set<String> variants = new HashSet<>();

    @NotNull
    @Column(nullable = false)
    private String icon;

    @Version
    private int version;

    TranslationLocaleEntity() {
    }

    public TranslationLocaleEntity(String language, String region, Collection<String> variants, String icon) {
        this.id = UUID.randomUUID().toString();
        this.language = language;
        this.region = region;
        this.variants.addAll(variants);
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public TranslationLocaleEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public TranslationLocaleEntity setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getRegion() {
        return region;
    }

    public TranslationLocaleEntity setRegion(String region) {
        this.region = region;
        return this;
    }

    public Set<String> getVariants() {
        return variants;
    }

    public TranslationLocaleEntity setVariants(Collection<String> variants) {
        this.variants.addAll(variants);
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public TranslationLocaleEntity setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public TranslationLocaleEntity setVersion(int version) {
        this.version = version;
        return this;
    }

    /**
     * Returns whether this locale and the specified one matches.
     */
    public boolean matchLocale(TranslationLocaleEntity other){
        return Objects.equals(getLanguage(), other.getLanguage())
                && Objects.equals(getRegion(), other.getRegion())
                && Objects.equals(getVariants(), other.getVariants());
    }
}
