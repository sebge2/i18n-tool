import {Repository} from "./repository.model";
import {GitHubRepositoryDto} from "../../../api";
import {RepositoryType} from "./repository-type.model";
import {RepositoryStatus} from "./repository-status.model";
import {TranslationsConfiguration} from "./translations-configuration.model";
import {BaseGitRepository} from "./base-git-repository.model";

export class GitHubRepository extends BaseGitRepository {

    public static fromDto(dto: GitHubRepositoryDto): Repository {
        return new GitHubRepository(
            dto.id,
            dto.name,
            RepositoryType[dto.type],
            RepositoryStatus[dto.status],
            TranslationsConfiguration.fromDto(dto.translationsConfiguration),
            dto.location,
            dto.defaultBranch,
            dto.allowedBranches,
            dto.autoSynchronized,
            dto.username,
            dto.repository
        );
    }

    constructor(id: string,
                name: string,
                type: RepositoryType,
                status: RepositoryStatus,
                translationsConfiguration: TranslationsConfiguration,
                location: string,
                defaultBranch: string,
                allowedBranches: string,
                autoSynchronized: boolean,
                public username: string,
                public repository: string) {
        super(id, name, type, status, translationsConfiguration, location, defaultBranch, allowedBranches, autoSynchronized);
    }

}
