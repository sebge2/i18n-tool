package be.sgerard.i18n.service.translator.dto;

import be.sgerard.i18n.model.translator.dto.ExternalTranslatorGenericRestConfigDto;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorGenericRestConfigEntity;
import be.sgerard.i18n.service.translator.ExternalTranslatorConfigType;
import org.springframework.stereotype.Component;

/**
 * {@link AbstractExternalTranslatorConfigDtoMapper DTO mapper} for generic REST translator config.
 *
 * @author Sebastien Gerard
 */
@Component
public class GenericRestExternalTranslatorConfigDtoMapper extends AbstractExternalTranslatorConfigDtoMapper<ExternalTranslatorGenericRestConfigEntity, ExternalTranslatorGenericRestConfigDto> {

    @Override
    public boolean support(ExternalTranslatorConfigType configType) {
        return configType == ExternalTranslatorConfigType.EXTERNAL_GENERIC_REST;
    }

    @Override
    public ExternalTranslatorGenericRestConfigDto mapToDto(ExternalTranslatorGenericRestConfigEntity config) {
        return fillDtoBuilder(ExternalTranslatorGenericRestConfigDto.builder(), config)
                .method(config.getMethod())
                .url(config.getUrl())
                .queryHeaders(config.getQueryHeaders())
                .queryParameters(config.getQueryParameters())
                .bodyTemplate(config.getBodyTemplate().orElse(null))
                .queryExtractor(config.getQueryExtractor())
                .build();
    }

    @Override
    public ExternalTranslatorGenericRestConfigEntity mapFromDto(ExternalTranslatorGenericRestConfigDto dto) {
        return fillFromDto(dto, new ExternalTranslatorGenericRestConfigEntity(dto.getLabel(), dto.getLinkUrl(), dto.getMethod(), dto.getUrl(), dto.getQueryExtractor()));
    }

    @Override
    public ExternalTranslatorGenericRestConfigEntity fillFromDto(ExternalTranslatorGenericRestConfigDto dto, ExternalTranslatorGenericRestConfigEntity entity) {
        return fillEntity(entity, dto)
                .setMethod(dto.getMethod())
                .setUrl(dto.getUrl())
                .setQueryHeaders(dto.getQueryHeaders())
                .setQueryParameters(dto.getQueryParameters())
                .setBodyTemplate(dto.getBodyTemplate())
                .setQueryExtractor(dto.getQueryExtractor());
    }
}
