package be.sgerard.i18n.model.snapshot.dto;

import be.sgerard.i18n.model.snapshot.SnapshotEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Optional;

/**
 * DTO representation of a {@link SnapshotEntity snapshot}.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "Snapshot", description = "Snapshot freezing the state of the tool in a ZIP file.")
@JsonDeserialize(builder = SnapshotDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class SnapshotDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SnapshotEntity snapshot) {
        return builder()
                .id(snapshot.getId())
                .createdBy(snapshot.getCreatedBy())
                .createdOn(snapshot.getCreatedOn())
                .comment(snapshot.getComment().orElse(null));
    }

    @Schema(description = "The unique id of this snapshot.", required = true)
    private final String id;

    @Schema(description = "The instant when this snapshot has been created.", required = true)
    private final Instant createdOn;

    @Schema(description = "The display name of the user that asked this snapshot.", required = true)
    private final String createdBy;

    @Schema(description = "Free end-user comment about this snapshot.")
    private final String comment;

    /**
     * @see #comment
     */
    public Optional<String> getComment() {
        return Optional.ofNullable(comment);
    }

    /**
     * Builder of {@link SnapshotDto snapshot}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
