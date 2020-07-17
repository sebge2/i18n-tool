import {WorkspaceStatus} from './workspace-status.model';
import {WorkspaceDto} from "../../api";

export class Workspace {

    constructor(private dto: WorkspaceDto) {
    }

    get id(): string {
        return this.dto.id;
    }

    get branch(): string {
        return this.dto.branch;
    }

    get status(): WorkspaceStatus {
        return WorkspaceStatus[this.dto.status];
    }

    isNotInitialized(): boolean {
        return this.dto.status == WorkspaceStatus.NOT_INITIALIZED;
    }

    isInitialized(): boolean {
        return this.dto.status == WorkspaceStatus.INITIALIZED;
    }

    isInReview(): boolean {
        return this.dto.status == WorkspaceStatus.IN_REVIEW;
    }

}
