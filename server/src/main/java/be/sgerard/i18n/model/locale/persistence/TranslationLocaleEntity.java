package be.sgerard.i18n.model.locale.persistence;

import be.sgerard.i18n.model.locale.dto.TranslationLocaleDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Locale in which translations must be available.
 *
 * @author Sebastien Gerard
 */
@Document("translation_locale")
@Getter
@Setter
@Accessors(chain = true)
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

    /**
     * The unique id of this entity.
     */
    @Id
    private String id;

    /**
     * The locale language (ex: fr).
     */
    @NotNull
    private String language;

    /**
     * The locale region (ex: BE).
     */
    private String region;

    /**
     * Locale variants.
     */
    @AccessType(AccessType.Type.PROPERTY)
    private final List<String> variants = new ArrayList<>();

    /**
     * The user friendly name for this locale.
     */
    private String displayName;

    /**
     * The icon associated to this locale (library flag-icon-css).
     */
    @NotNull
    private String icon;

    @PersistenceConstructor
    TranslationLocaleEntity() {
    }

    public TranslationLocaleEntity(String language,
                                   String region,
                                   Collection<String> variants,
                                   String displayName,
                                   String icon) {
        this.id = UUID.randomUUID().toString();
        this.language = language;
        this.region = region;
        this.variants.addAll(variants);
        this.displayName = displayName;
        this.icon = icon;
    }

    /**
     * @see #region
     */
    public Optional<String> getRegion() {
        return Optional.ofNullable(region);
    }

    /**
     * Sets locale variants.
     */
    public TranslationLocaleEntity setVariants(Collection<String> variants) {
        this.variants.addAll(variants);
        return this;
    }

    /**
     * @see #displayName
     */
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
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
