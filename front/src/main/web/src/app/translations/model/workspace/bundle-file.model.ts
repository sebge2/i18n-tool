import {BundleFileDto} from "../../../api";
import {BundleFileEntry} from "./bundle-file-entry.model";

export class BundleFile {

    public static fromDto(dto: BundleFileDto): BundleFile {
        return new BundleFile(dto.id, dto.name, dto.location, BundleType[dto.type], dto.files.map(file => BundleFileEntry.fromDto(file)));
    }

    constructor(public id: string,
                public name: string,
                public location: string,
                public type: BundleType,
                public files: BundleFileEntry[]) {
    }

}

export enum BundleType {

    JAVA_PROPERTIES = "JAVA_PROPERTIES",

    JSON_ICU = "JSON_ICU"
}
