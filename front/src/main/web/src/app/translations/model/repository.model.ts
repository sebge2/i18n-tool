import {RepositoryStatus} from "./repository-status.model";

export class Repository {

    private readonly _status: RepositoryStatus;

    constructor(repository: Repository = <Repository>{}) {
        this._status = repository._status;
    }

    get status(): RepositoryStatus {
        return this._status;
    }

    isNotInitialized(): boolean {
        return status == RepositoryStatus.NOT_INITIALIZED;
    }

    isInitializing(): boolean {
        return status == RepositoryStatus.INITIALIZING;
    }

    isInitialized(): boolean {
        return status == RepositoryStatus.INITIALIZED;
    }

}
