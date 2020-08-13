import {Component, Input} from '@angular/core';
import {WorkspaceTreeNode} from "../repository-details-workspaces.component";

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

}
