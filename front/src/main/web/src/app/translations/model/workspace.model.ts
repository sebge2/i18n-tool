import {WorkspaceStatus} from './workspace-status.model';

export class Workspace {

    readonly id: string;
    readonly branch: string;
    readonly status: WorkspaceStatus;
    readonly pullRequestBranch: string;
    readonly pullRequestNumber: number;
    readonly initializationTime: number;

    constructor(user: Workspace = <Workspace>{}) {
        this.id = user.id;
        this.branch = user.branch;
        this.status = user.status;
        this.pullRequestBranch = user.pullRequestBranch;
        this.pullRequestNumber = user.pullRequestNumber;
        this.initializationTime = user.initializationTime;
    }

}
