import {Component, Input} from '@angular/core';
import {WorkspaceTreeNode} from "../repository-details-workspaces.component";
import {WorkspaceStatus} from "../../../../../../translations/model/workspace/workspace-status.model";
import {WorkspaceService} from "../../../../../../translations/service/workspace.service";
import {NotificationService} from "../../../../../../core/notification/service/notification.service";

@Component({
    selector: 'app-repository-details-workspace-node',
    templateUrl: './repository-details-workspace-node.component.html',
    styleUrls: ['./repository-details-workspace-node.component.css']
})
export class RepositoryDetailsWorkspaceNodeComponent {

    @Input() public node: WorkspaceTreeNode;
    @Input() public loading: boolean = false;

    public initializationInProgress: boolean = false;
    public deleteInProgress: boolean = false;

    constructor(private _workspaceService: WorkspaceService,
                private _notificationService: NotificationService) {
    }

    public get name(): string {
        return this.node.workspace.branch;
    }

    public get badgeClass(): string {
        switch(this.node.workspace.status){
            case WorkspaceStatus.IN_REVIEW:
            case WorkspaceStatus.INITIALIZED:
                return 'app-badge-success';
            case WorkspaceStatus.NOT_INITIALIZED:
                return 'app-badge-warning';
            default:
                return '';
        }
    }

    public get badgeText(): string {
        switch(this.node.workspace.status){
            case WorkspaceStatus.IN_REVIEW:
                return 'ADMIN.REPOSITORIES.DETAILS_CARD.WORKSPACES.IN_REVIEW';
            case WorkspaceStatus.INITIALIZED:
                return 'ADMIN.REPOSITORIES.DETAILS_CARD.WORKSPACES.READY';
            case WorkspaceStatus.NOT_INITIALIZED:
                return 'ADMIN.REPOSITORIES.DETAILS_CARD.WORKSPACES.NOT_READY';
            default:
                return '';
        }
    }

    public get initializationAllowed(){
        return this.node.workspace.status === WorkspaceStatus.NOT_INITIALIZED;
    }

    public onInitialize() {
        this.initializationInProgress = true;

        this._workspaceService
            .initialize(this.node.workspace.id)
            .toPromise()
            .catch(error => {
                console.error('Error while initializing workspace.', error);
                this._notificationService.displayErrorMessage('ADMIN.REPOSITORIES.ERROR.INITIALIZE_WORKSPACE', error);
            })
            .finally(() => this.initializationInProgress = false);
    }

    public onDelete() {
        this.deleteInProgress = true;

        this._workspaceService
            .delete(this.node.workspace)
            .toPromise()
            .catch(error => {
                console.error('Error while deleting workspace.', error);
                this._notificationService.displayErrorMessage('ADMIN.REPOSITORIES.ERROR.DELETE_WORKSPACE', error);
            })
            .finally(() => this.deleteInProgress = false);
    }
}
