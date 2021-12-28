import {TextTranslationDto} from "../../../../api";
import {ExternalTranslationSource} from "./external-translation-source.model";
import * as _ from "lodash";

export class TextTranslation {

    static fromDto(dto: TextTranslationDto, sources: Map<string, ExternalTranslationSource>): TextTranslation {
        return new TextTranslation(
            !_.isNil(dto.externalSource)
                ? sources.get(dto.externalSource)
                : null,
            dto.originalText,
            dto.text
        );
    }

    constructor(public externalSource: ExternalTranslationSource | undefined,
                public originalText: string,
                public text: string) {
    }
}