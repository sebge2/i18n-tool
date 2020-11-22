package be.sgerard.i18n.model.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Update of the current user password.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "CurrentUserPasswordUpdate", description = "The update of the current user password.")
@Getter
public class CurrentUserPasswordUpdateDto {

    @Schema(description = "The current user's password.")
    private final String currentPassword;

    @Schema(description = "The new user's password.")
    private final String newPassword;

    @JsonCreator
    public CurrentUserPasswordUpdateDto(@JsonProperty("currentPassword") String currentPassword,
                                        @JsonProperty("newPassword") String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}
