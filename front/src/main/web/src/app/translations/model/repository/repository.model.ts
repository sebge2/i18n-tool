import {RepositoryStatus} from "./repository-status.model";
import {RepositoryType} from "./repository-type.model";
import {TranslationsConfiguration} from "./translations-configuration.model";

export class Repository {

    constructor(public id: string,
                public name: string,
                public type: RepositoryType,
                public status: RepositoryStatus,
                public translationsConfiguration: TranslationsConfiguration,
                public autoSynchronized: boolean) {
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
}
