import {ExternalTranslationSourceDto} from "../../../../api";

export class ExternalTranslationSource {

    static fromDto(dto: ExternalTranslationSourceDto): ExternalTranslationSource {
        return new ExternalTranslationSource(dto.id, dto.label, dto.url);
    }

    constructor(public id: string,
                public label: string,
                public url: string) {
    }

}