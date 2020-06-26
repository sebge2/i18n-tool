package be.sgerard.i18n.model.security.user.persistence;

import be.sgerard.i18n.model.ToolLocale;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

/**
 * Entity for user preferences.
 *
 * @author Sebastien Gerard
 */
@Entity(name = "user_preferences")
public class UserPreferencesEntity {

    @Id
    private String id;

    @NotNull
    @OneToOne
    private UserEntity user;

    @Enumerated(EnumType.STRING)
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
