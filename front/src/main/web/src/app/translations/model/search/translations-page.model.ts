import {TranslationsPageDto} from "../../../api";
import {TranslationsPageRow} from "./translations-page-row.model";
import * as _ from "lodash";

export class TranslationsPage {

    public static fromDto(dto: TranslationsPageDto): TranslationsPage {
        return new TranslationsPage(
            dto.rows.map(row => TranslationsPageRow.fromDto(row)),
            dto.locales,
            dto.firstPageKey,
            dto.lastPageKey
        )
    }

    constructor(public rows: TranslationsPageRow[],
                public locales: string[],
                public firstPageKey: string,
                public lastPageKey: string) {
    }

    public get hasRows(): boolean {
        return _.some(this.rows);
    }

    public get empty(): boolean {
        return _.isEmpty(this.rows);
    }
}
