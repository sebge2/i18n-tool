import {WorkspaceStatus} from './workspace-status.model';

export class Workspace {

    readonly id: string;
    readonly branch: string;
    readonly status: WorkspaceStatus;

    constructor(workspace: Workspace = <Workspace>{}) {
        Object.assign(this, workspace);
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
