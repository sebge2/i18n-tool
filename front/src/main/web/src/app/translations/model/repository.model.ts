import {RepositoryStatus} from "./repository-status.model";
import {RepositoryDto} from "../../api";

export class Repository {

    constructor(private dto: RepositoryDto) {
    }

    get id(): string {
        return this.dto.id;
    }

    isNotInitialized(): boolean {
        return this.dto.status == RepositoryStatus.NOT_INITIALIZED;
    }

    isInitializing(): boolean {
        return this.dto.status == RepositoryStatus.INITIALIZING;
    }

    isInitialized(): boolean {
        return this.dto.status == RepositoryStatus.INITIALIZED;
    }

}
