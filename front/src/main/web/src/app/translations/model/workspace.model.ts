import {WorkspaceStatus} from './workspace-status.model';
import {WorkspaceDto} from "../../api";

export class Workspace {

    constructor(private dto: WorkspaceDto) {
    }

    public get id(): string {
        return this.dto.id;
    }

    public get branch(): string {
        return this.dto.branch;
    }

    public get status(): WorkspaceStatus {
        return WorkspaceStatus[this.dto.status];
    }

    public isNotInitialized(): boolean {
        return this.dto.status == WorkspaceStatus.NOT_INITIALIZED;
    }

    public isInitialized(): boolean {
        return this.dto.status == WorkspaceStatus.INITIALIZED;
    }

    public isInReview(): boolean {
        return this.dto.status == WorkspaceStatus.IN_REVIEW;
    }

    public equals(other: Workspace): boolean {
        return this.id === other.id;
    }
}
