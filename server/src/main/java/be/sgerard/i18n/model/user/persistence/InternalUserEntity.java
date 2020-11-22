package be.sgerard.i18n.model.user.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

/**
 * Internal {@link UserEntity user}.
 *
 * @author Sebastien Gerard
 */
@Document("user")
@TypeAlias("internal")
@Getter
@Setter
@Accessors(chain = true)
public class InternalUserEntity extends UserEntity {

    /**
     * The encoded password.
     */
    @NotNull
    private String password;

    /**
     * The avatar image (PNG image).
     */
    @NotNull
    private byte[] avatar;

    @PersistenceConstructor
    InternalUserEntity() {
    }

    public InternalUserEntity(String username, String displayName) {
        setId(UUID.randomUUID().toString());
        setUsername(username);
        setDisplayName(displayName);
        setPreferences(new UserPreferencesEntity());
    }

    /**
     * @see #avatar
     */
    public Optional<byte[]> getAvatar() {
        return Optional.ofNullable(avatar);
    }

    /**
     * Returns whether an avatar is available.
     */
    public boolean hasAvatar() {
        return getAvatar().map(bytes -> bytes.length > 0).orElse(false);
    }
}
