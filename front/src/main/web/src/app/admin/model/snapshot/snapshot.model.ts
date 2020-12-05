import {SnapshotDto} from "../../../api";

export class Snapshot {

    public static fromDto(dto: SnapshotDto): Snapshot {
        return new Snapshot(
            dto.id,
            dto.createdOn,
            dto.createdBy,
            dto.comment
        );
    }

    constructor(public id: string,
                public createdOn: Date,
                public createdBy: string,
                public comment?: string) {
    }
}
