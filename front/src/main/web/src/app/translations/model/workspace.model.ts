import {WorkspaceStatus} from './workspace-status.model';

export class Workspace {

    readonly id: string;
    readonly branch: string;
    readonly status: WorkspaceStatus;
    readonly pullRequestBranch: string;
    readonly pullRequestNumber: number;
    readonly initializationTime: number;

    constructor(workspace: Workspace = <Workspace>{}) {
        this.id = workspace.id;
        this.branch = workspace.branch;
        this.status = workspace.status;
        this.pullRequestBranch = workspace.pullRequestBranch;
        this.pullRequestNumber = workspace.pullRequestNumber;
        this.initializationTime = workspace.initializationTime;
    }

    isNotInitialized(): boolean {
        return this.status == WorkspaceStatus.NOT_INITIALIZED;
    }

    isInitialized(): boolean {
        return this.status == WorkspaceStatus.INITIALIZED;
    }

    isInReview(): boolean {
        return this.status == WorkspaceStatus.IN_REVIEW;
    }

}
