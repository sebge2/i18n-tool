import {TranslationsPageDto} from "../../../api";
import {TranslationsPageRow} from "./translations-page-row.model";

export class TranslationsPage {

    public static fromDto(dto: TranslationsPageDto): TranslationsPage {
        return new TranslationsPage(
            dto.rows.map(row => TranslationsPageRow.fromDto(row)),
            dto.locales,
            dto.pageIndex
        )
    }

    constructor(public rows: TranslationsPageRow[],
                public locales: string[],
                public pageIndex: number) {
    }
}
