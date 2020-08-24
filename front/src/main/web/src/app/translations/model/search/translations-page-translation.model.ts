import {TranslationsPageTranslationDto} from "../../../api";

export class TranslationsPageTranslation {

    public static fromDto(dto: TranslationsPageTranslationDto): TranslationsPageTranslation {
        return new TranslationsPageTranslation(dto.originalValue, dto.updatedValue, dto.lastEditor);
    }

    constructor(public originalValue: string,
                public updatedValue?: string,
                public lastEditor?: string) {
    }
}
