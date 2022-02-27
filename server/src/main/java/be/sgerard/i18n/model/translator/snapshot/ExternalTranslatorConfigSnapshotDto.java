package be.sgerard.i18n.model.translator.snapshot;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.service.translator.ExternalTranslatorConfigType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Dto for storing a {@link ExternalTranslatorConfigEntity translator config} in a snapshot.
 *
 * @author Sebastien Gerard
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExternalTranslatorGenericRestConfigSnapshotDto.class, name = ExternalTranslatorGenericRestConfigSnapshotDto.TYPE)
})
@Getter
@SuperBuilder
public abstract class ExternalTranslatorConfigSnapshotDto {

    /**
     * @see ExternalTranslatorConfigEntity#getId()
     */
    private final String id;

    /**
     * @see ExternalTranslatorConfigEntity#getLabel()
     */
    private final String label;

    /**
     * @see ExternalTranslatorConfigEntity#getLinkUrl()
     */
    private final String linkUrl;

    /**
     * Returns the {@link ExternalTranslatorConfigType type} of this configuration.
     */
    public abstract ExternalTranslatorConfigType getType();

    /**
     * Builder of {@link ExternalTranslatorConfigSnapshotDto translator config snapshot}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder {
    }
}