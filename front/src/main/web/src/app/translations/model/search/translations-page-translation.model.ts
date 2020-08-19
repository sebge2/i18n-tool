import {TranslationsPageTranslationDto} from "../../../api";

export class TranslationsPageTranslation {

    public static fromDto(dto: TranslationsPageTranslationDto): TranslationsPageTranslation {
        return new TranslationsPageTranslation(dto.id, dto.originalValue, dto.updatedValue, dto.lastEditor);
    }

    constructor(public id: string,
                public originalValue: string,
                public updatedValue?: string,
                public lastEditor?: string) {
    }
}
