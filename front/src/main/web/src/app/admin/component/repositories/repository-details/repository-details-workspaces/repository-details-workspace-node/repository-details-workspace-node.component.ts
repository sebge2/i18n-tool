import {Component, Input} from '@angular/core';
import {WorkspaceTreeNode} from "../repository-details-workspaces.component";
import {WorkspaceStatus} from "../../../../../../translations/model/workspace/workspace-status.model";

@Component({
    selector: 'app-repository-details-workspace-node',
    templateUrl: './repository-details-workspace-node.component.html',
    styleUrls: ['./repository-details-workspace-node.component.css']
})
export class RepositoryDetailsWorkspaceNodeComponent {

    @Input() public node: WorkspaceTreeNode;
    constructor() {
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

}
