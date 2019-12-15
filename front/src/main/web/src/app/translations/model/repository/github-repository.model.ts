import {Repository} from "./repository.model";
import {GitHubRepositoryDto} from "../../../api";
import {RepositoryType} from "./repository-type.model";
import {RepositoryStatus} from "./repository-status.model";
import {GitRepository} from "./git-repository.model";

export class GitHubRepository extends GitRepository {

    public static fromDto(dto: GitHubRepositoryDto): Repository {
        return new GitHubRepository(
            dto.id,
            dto.name,
            RepositoryType[dto.type],
            RepositoryStatus[dto.status],
            dto.location,
            dto.defaultBranch,
            dto.allowedBranches,
            dto.username,
            dto.repository
        );
    }

    constructor(id: string,
                name: string,
                type: RepositoryType,
                status: RepositoryStatus,
                location: string,
                defaultBranch: string,
                allowedBranches: string,
                public username: string,
                public repository: string) {
        super(id, name, type, status, location, defaultBranch, allowedBranches);
    }

}
