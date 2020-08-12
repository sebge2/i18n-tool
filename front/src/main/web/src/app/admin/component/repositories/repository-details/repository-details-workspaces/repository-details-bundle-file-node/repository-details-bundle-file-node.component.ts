import {Component, Input} from '@angular/core';
import {WorkspaceBundleTreeNode} from "../repository-details-workspaces.component";

@Component({
    selector: 'app-repository-details-bundle-file-node',
    templateUrl: './repository-details-bundle-file-node.component.html',
    styleUrls: ['./repository-details-bundle-file-node.component.css']
})
export class RepositoryDetailsBundleFileNodeComponent {

    @Input() public node: WorkspaceBundleTreeNode;

    constructor() {
    }

}
