import {RepositoryStatus} from "./repository-status.model";

export class Repository {

    readonly status: RepositoryStatus;

    constructor(repository: Repository = <Repository>{}) {
        Object.assign(this, repository);
    }

    isNotInitialized(): boolean {
        return this.status == RepositoryStatus.NOT_INITIALIZED;
    }

    isInitializing(): boolean {
        return this.status == RepositoryStatus.INITIALIZING;
    }

    isInitialized(): boolean {
        return this.status == RepositoryStatus.INITIALIZED;
    }

}
