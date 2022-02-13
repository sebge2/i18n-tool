package be.sgerard.i18n.model.user.persistence;

import be.sgerard.i18n.model.ToolLocale;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Entity for user preferences.
 *
 * @author Sebastien Gerard
 */
@Getter
@Setter
@Accessors(chain = true)
public class UserPreferencesEntity {

    /**
     * The {@link ToolLocale locale} to use for this user.
     */
    private ToolLocale toolLocale;

    /**
     * {@link TranslationLocaleEntity#getId() Locales} that are preferred/spoken by the end-user.
     */
    @AccessType(AccessType.Type.PROPERTY)
    private final List<String> preferredLocales = new ArrayList<>();

    @PersistenceConstructor
    UserPreferencesEntity() {
    }

    /**
     * @see #toolLocale
     */
    public Optional<ToolLocale> getToolLocale() {
        return Optional.ofNullable(toolLocale);
    }

    /**
     * @see #preferredLocales
     */
    public UserPreferencesEntity setPreferredLocales(List<String> preferredLocales) {
        this.preferredLocales.clear();
        this.preferredLocales.addAll(preferredLocales);
        return this;
    }
}
