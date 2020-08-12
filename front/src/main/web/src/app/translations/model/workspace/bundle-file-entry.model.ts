import {BundleFileEntryDto} from "../../../api";

export class BundleFileEntry {

    public static fromDto(dto: BundleFileEntryDto): BundleFileEntry {
        return new BundleFileEntry(dto.id, dto.file, dto.locale);
    }

    constructor(public id: string,
                public file: string,
                public locale: string) {

    }

}
