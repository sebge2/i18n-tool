package be.sgerard.i18n.model.snapshot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

/**
 * Request for the creation of a new snapshot.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "SnapshotCreation", description = "Request for the creation of a new snapshot.")
@JsonDeserialize(builder = SnapshotCreationDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class SnapshotCreationDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "Free comment describing the snapshot.")
    private final String comment;

    @Schema(description = "Password used to encrypt the ZIP file.")
    private final String encryptionPassword;

    /**
     * @see #comment
     */
    public Optional<String> getComment() {
        return Optional.ofNullable(comment);
    }

    /**
     * @see #encryptionPassword
     */
    public Optional<String> getEncryptionPassword() {
        return Optional.ofNullable(encryptionPassword).filter(StringUtils::hasText);
    }

    /**
     * Builder of {@link SnapshotCreationDto snapshot}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
