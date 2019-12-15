import {Repository} from "../repository/repository.model";
import {Workspace} from "./workspace.model";

export class EnrichedWorkspace {

    constructor(public repository: Repository, public workspace: Workspace) {
    }

    public get defaultWorkspace(): boolean {
        return this.workspace.branch === this.repository.defaultBranch;
    }

}
