import {Repository} from "./repository.model";
import {GitRepositoryDto} from "../../../api";
import {RepositoryType} from "./repository-type.model";
import {RepositoryStatus} from "./repository-status.model";

export class GitRepository extends Repository {

    public static fromDto(dto: GitRepositoryDto): Repository {
        return new GitRepository(
            dto.id,
            dto.name,
            RepositoryType[dto.type],
            RepositoryStatus[dto.status],
            dto.defaultBranch
        );
    }

    constructor(public id: string,
                public name: string,
                public type: RepositoryType,
                public status: RepositoryStatus,
                public defaultBranch: string) {
        super(id, name, type, status);
    }

}
