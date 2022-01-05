package be.sgerard.i18n.model.translator.persistence;

import be.sgerard.i18n.service.translator.ExternalTranslatorConfigType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * External (not from this tool) source of translation.
 *
 * @author Sebastien Gerard
 */
@Document("external_translator_config")
@Getter
@Setter
@Accessors(chain = true)
public abstract class ExternalTranslatorConfigEntity {

    /**
     * The unique id of this source.
     */
    @Id
    private String id;

    /**
     * Label associated to this source.
     */
    @NotNull
    private String label;

    /**
     * Link URL describing this source.
     */
    @NotNull
    private String linkUrl;

    ExternalTranslatorConfigEntity() {
    }

    public ExternalTranslatorConfigEntity(String label, String linkUrl) {
        setId(UUID.randomUUID().toString());

        this.label = label;
        this.linkUrl = linkUrl;
    }

    /**
     * Returns the {@link ExternalTranslatorConfigType configuration type}.
     */
    public abstract ExternalTranslatorConfigType getType();
}
