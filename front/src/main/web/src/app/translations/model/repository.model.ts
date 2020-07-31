import {RepositoryStatus} from "./repository-status.model";
import {RepositoryDto} from "../../api";
import {RepositoryType} from "./repository-type.model";

export class Repository {

    public static fromDto(dto: RepositoryDto): Repository {
        return new Repository(
            dto.id,
            dto.name,
            RepositoryType[dto.type],
            RepositoryStatus[dto.status]
        );
    }

    public static create(): Repository {
        return new Repository(null, null, null, null);
    }

    constructor(public id: string,
                public name: string,
                public type: RepositoryType,
                public status: RepositoryStatus) {
    }

    public isNotInitialized(): boolean {
        return this.status == RepositoryStatus.NOT_INITIALIZED;
    }

    public isInitializing(): boolean {
        return this.status == RepositoryStatus.INITIALIZING;
    }

    public isInitialized(): boolean {
        return this.status == RepositoryStatus.INITIALIZED;
    }

    public toDto(): RepositoryDto {
        return {
            id: this.id,
            name: this.name,
            type: RepositoryDto.TypeDtoEnum[this.type],
            status: this.status
        }
    }
}
