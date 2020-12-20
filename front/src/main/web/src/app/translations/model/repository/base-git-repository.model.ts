import {Repository} from "./repository.model";
import {RepositoryType} from "./repository-type.model";
import {RepositoryStatus} from "./repository-status.model";
import {TranslationsConfiguration} from "./translations-configuration.model";

export class BaseGitRepository extends Repository {

    constructor(id: string,
                name: string,
                type: RepositoryType,
                status: RepositoryStatus,
                translationsConfiguration: TranslationsConfiguration,
                public location: string,
                public defaultBranch: string,
                public allowedBranches: string) {
        super(id, name, type, status, translationsConfiguration);
    }

}
