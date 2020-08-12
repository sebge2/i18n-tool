import {Component} from '@angular/core';
import {TreeNode} from "../../../../../../core/shared/component/tree/tree.component";

@Component({
    selector: 'app-repository-details-workspace-tree-node',
    templateUrl: './repository-details-workspace-tree-node.component.html',
    styleUrls: ['./repository-details-workspace-tree-node.component.css']
})
export class RepositoryDetailsWorkspaceTreeNodeComponent {

    public node: TreeNode;

    constructor() {
    }

}
