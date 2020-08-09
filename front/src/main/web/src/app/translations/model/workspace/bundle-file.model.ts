import {BundleFileDto} from "../../../api";

export class BundleFile {

    public static fromDto(dto: BundleFileDto): BundleFile {
        return new BundleFile(dto.id, dto.name, dto.location, BundleType[dto.type]);
    }

    constructor(public id: string,
                public name: string,
                public location: string,
                public type: BundleType) {
    }

}

export enum BundleType {

    JAVA_PROPERTIES = "JAVA_PROPERTIES",

    JSON_ICU = "JSON_ICU"
}
