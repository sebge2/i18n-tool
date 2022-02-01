package be.sgerard.i18n.service.translator.snapshot;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorGenericRestConfigEntity;
import be.sgerard.i18n.model.translator.snapshot.ExternalTranslatorConfigSnapshotDto;
import be.sgerard.i18n.model.translator.snapshot.ExternalTranslatorGenericRestConfigSnapshotDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link AbstractExternalTranslatorConfigSnapshotDtoMapper Mapper} of {@link ExternalTranslatorGenericRestConfigEntity external translator config} to
 * a {@link ExternalTranslatorGenericRestConfigSnapshotDto snapshot} and the other way around.
 *
 * @author Sebastien Gerard
 */
@Component
public class ExternalTranslatorGenericRestConfigSnapshotDtoMapper extends AbstractExternalTranslatorConfigSnapshotDtoMapper<ExternalTranslatorGenericRestConfigEntity, ExternalTranslatorGenericRestConfigSnapshotDto> {

    @Override
    public boolean support(ExternalTranslatorConfigSnapshotDto dto) {
        return dto instanceof ExternalTranslatorGenericRestConfigSnapshotDto;
    }

    @Override
    public boolean support(ExternalTranslatorConfigEntity entity) {
        return entity instanceof ExternalTranslatorGenericRestConfigEntity;
    }

    @Override
    public Mono<ExternalTranslatorGenericRestConfigEntity> mapFromDto(ExternalTranslatorGenericRestConfigSnapshotDto dto) {
        final ExternalTranslatorGenericRestConfigEntity entity = new ExternalTranslatorGenericRestConfigEntity();

        fillEntity(entity, dto);

        return Mono.just(
                entity
                        .setMethod(dto.getMethod())
                        .setUrl(dto.getUrl())
                        .setQueryParameters(dto.getQueryParameters())
                        .setQueryHeaders(dto.getQueryHeaders())

                        .setBodyTemplate(dto.getBodyTemplate().orElse(null))
                        .setQueryExtractor(dto.getQueryExtractor())
        );
    }

    @Override
    public Mono<ExternalTranslatorGenericRestConfigSnapshotDto> mapToDto(ExternalTranslatorGenericRestConfigEntity entity) {
        final ExternalTranslatorGenericRestConfigSnapshotDto.ExternalTranslatorGenericRestConfigSnapshotDtoBuilder<?, ?> dtoBuilder = ExternalTranslatorGenericRestConfigSnapshotDto.builder();

        fillDtoBuilder(dtoBuilder, entity);

        return Mono.just(
                dtoBuilder

                        .method(entity.getMethod())
                        .url(entity.getUrl())
                        .queryParameters(entity.getQueryParameters())
                        .queryHeaders(entity.getQueryHeaders())

                        .bodyTemplate(entity.getBodyTemplate().orElse(null))
                        .queryExtractor(entity.getQueryExtractor())
                        .build()
        );
    }
}
