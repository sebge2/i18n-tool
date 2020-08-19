import {TranslationsPageRowDto} from "../../../api";
import {TranslationsPageTranslation} from "./translations-page-translation.model";

export class TranslationsPageRow {

    public static fromDto(dto: TranslationsPageRowDto): TranslationsPageRow {
        return new TranslationsPageRow(
            dto.workspace,
            dto.bundleFile,
            dto.bundleKey,
            dto.translations.map(translation => TranslationsPageTranslation.fromDto(translation))
        )
    }

    constructor(public workspace: string,
                public bundleFile: string,
                public bundleKey: string,
                public translations: TranslationsPageTranslation[]) {
    }
}
