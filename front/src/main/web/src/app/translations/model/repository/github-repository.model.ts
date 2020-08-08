import {Repository} from "./repository.model";
import {GitHubRepositoryDto} from "../../../api";
import {RepositoryType} from "./repository-type.model";
import {RepositoryStatus} from "./repository-status.model";

export class GitHubRepository extends Repository {

    public static fromDto(dto: GitHubRepositoryDto): Repository {
        return new GitHubRepository(
            dto.id,
            dto.name,
            RepositoryType[dto.type],
            RepositoryStatus[dto.status],
            dto.location,
            dto.defaultBranch,
            dto.accessKey,
            dto.webHookSecret
        );
    }

    constructor(public id: string,
                public name: string,
                public type: RepositoryType,
                public status: RepositoryStatus,
                public location: string,
                public defaultBranch: string,
                public accessKey: string,
                public webHookSecret: string) {
        super(id, name, type, status);
    }

}
