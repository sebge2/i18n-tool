package be.sgerard.i18n.model.security.session.persistence;

import be.sgerard.i18n.model.user.persistence.UserEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

/**
 * Description of a user live session.
 *
 * @author Sebastien Gerard
 */
@Document("user_live_session")
@Getter
@Setter
@Accessors(chain = true)
public class UserLiveSessionEntity {

    /**
     * The unique id of this live session.
     */
    @Id
    private String id;

    /**
     * The associated {@link UserEntity#getId() user}.
     */
    @NotNull
    private String user;

    /**
     * The time when the user logged in.
     */
    @NotNull
    private Instant loginTime;

    /**
     * The time when the live session ends.
     */
    private Instant logoutTime;

    @PersistenceConstructor
    UserLiveSessionEntity() {
    }

    public UserLiveSessionEntity(UserEntity user) {
        this.user = user.getId();
        this.id = UUID.randomUUID().toString();
        this.loginTime = Instant.now();
    }

}
