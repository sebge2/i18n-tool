export class Repository {

    readonly initialized: boolean;

    constructor(repository: Repository = <Repository>{}) {
        this.initialized = repository.initialized;
    }

    isInitialized(): boolean {
        return this.initialized;
    }

}
