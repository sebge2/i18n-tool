import {RepositoryStatus} from "./repository-status.model";

export class Repository {

    readonly status: RepositoryStatus;

    constructor(repository: Repository = <Repository>{}) {
        this.status = repository.status;
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
