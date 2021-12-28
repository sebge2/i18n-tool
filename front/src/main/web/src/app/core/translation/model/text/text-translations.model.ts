import {TextTranslationResponseDto} from "../../../../api";
import {TextTranslation} from "./text-translation.model";
import {ExternalTranslationSource} from "./external-translation-source.model";
import * as _ from "lodash";

export class TextTranslations {

    static fromDto(dto: TextTranslationResponseDto): TextTranslations {
        const sources = new Map<string, ExternalTranslationSource>();
        _.forEach(dto.externalSources, sourceDto => sources.set(sourceDto.id, ExternalTranslationSource.fromDto(sourceDto)));

        return new TextTranslations(
            _.map(
                dto.translations,
                translationDto => TextTranslation.fromDto(translationDto, sources)
            )
        );
    }

    constructor(public translations: TextTranslation[] = []) {
    }
}