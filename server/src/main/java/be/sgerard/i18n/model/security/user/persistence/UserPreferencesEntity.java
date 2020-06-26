package be.sgerard.i18n.model.security.user.persistence;

import be.sgerard.i18n.model.ToolLocale;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.Optional;

/**
 * Entity for user preferences.
 *
 * @author Sebastien Gerard
 */
public class UserPreferencesEntity {

    @Id
    private String id;

    private ToolLocale toolLocale;

    @PersistenceConstructor
    UserPreferencesEntity() {
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
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the {@link ToolLocale locale} to use for this user.
     */
    public Optional<ToolLocale> getToolLocale() {
        return Optional.ofNullable(toolLocale);
    }

    /**
     * Sets the {@link ToolLocale locale} to use for this user.
     */
    public void setToolLocale(ToolLocale toolLocale) {
        this.toolLocale = toolLocale;
    }
}
