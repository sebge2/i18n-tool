package be.sgerard.i18n.model.snapshot.file;

import be.sgerard.i18n.model.snapshot.SnapshotEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Optional;

/**
 * Metadata of a snapshot.
 *
 * @author Sebastien Gerard
 */
@JsonDeserialize(builder = SnapshotMetadataDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class SnapshotMetadataDto {

    /**
     * @see SnapshotEntity#getId()
     */
    private final String id;

    /**
     * @see SnapshotEntity#getCreatedOn()
     */
    private final Instant createdOn;

    /**
     * @see SnapshotEntity#getCreatedBy()
     */
    private final String createdBy;

    /**
     * @see SnapshotEntity#getComment()
     */
    private final String comment;

    /**
     * @see #comment
     */
    public Optional<String> getComment() {
        return Optional.ofNullable(comment);
    }

    /**
     * Builder of {@link SnapshotMetadataDto snapshot metadata}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
