package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Locale in which translations must be available.
 *
 * @author Sebastien Gerard
 */
@Entity(name = "translation_locale")
public class TranslationLocaleEntity {

    /**
     * Returns the user representation of the following fields composing a locale.
     */
    public static String toUserString(String language, String region, Collection<String> variants) {
        return language + (StringUtils.isEmpty(region) ? "" : "-" + region.toUpperCase()) + (variants.isEmpty() ? "" : " " + variants);
    }

    /**
     * Returns the Java {@link Locale locale}.
     *
     * @param region can be <tt>null</tt>
     */
    public static Locale toLocale(String language, String region, List<String> variants) {
        if (region != null) {
            if (variants.isEmpty()) {
                return new Locale(language, region);
            } else {
                return new Locale(language, region, String.join("_", variants));
            }
        } else {
            return new Locale(language);
        }
    }

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
    private final List<String> variants = new ArrayList<>();

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

    /**
     * Returns the unique id of this entity.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique id of this entity.
     */
    public TranslationLocaleEntity setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the locale language (ex: fr).
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the locale language (ex: fr).
     */
    public TranslationLocaleEntity setLanguage(String language) {
        this.language = language;
        return this;
    }

    /**
     * Returns the locale region (ex: BE).
     */
    public Optional<String> getRegion() {
        return Optional.ofNullable(region);
    }

    /**
     * Sets the locale region (ex: BE).
     */
    public TranslationLocaleEntity setRegion(String region) {
        this.region = region;
        return this;
    }

    /**
     * Returns locale variants.
     */
    public List<String> getVariants() {
        return variants;
    }

    /**
     * Sets locale variants.
     */
    public TranslationLocaleEntity setVariants(Collection<String> variants) {
        this.variants.addAll(variants);
        return this;
    }

    /**
     * Returns the icon associated to this locale (library flag-icon-css).
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Sets the icon associated to this locale (library flag-icon-css).
     */
    public TranslationLocaleEntity setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Returns this entity as a {@link Locale locale}.
     */
    public Locale toLocale() {
        return toLocale(getLanguage(), getRegion().orElse(null), getVariants());
    }

    /**
     * Returns whether this locale and the specified one matches.
     */
    public boolean matchLocale(TranslationLocaleEntity other) {
        return Objects.equals(getLanguage(), other.getLanguage())
                && Objects.equals(getRegion(), other.getRegion())
                && Objects.equals(new ArrayList<>(getVariants()), new ArrayList<>(other.getVariants()));
    }

    /**
     * Returns whether this locale and the specified one matches.
     */
    public boolean matchLocale(TranslationLocaleDto other) {
        return Objects.equals(getLanguage(), other.getLanguage())
                && Objects.equals(getRegion(), other.getRegion())
                && Objects.equals(new ArrayList<>(getVariants()), new ArrayList<>(other.getVariants()));
    }
}
