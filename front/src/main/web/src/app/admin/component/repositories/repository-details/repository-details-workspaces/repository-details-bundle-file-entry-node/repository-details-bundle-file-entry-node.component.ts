import {Component, Input} from '@angular/core';
import {WorkspaceBundleFileEntryTreeNode} from "../repository-details-workspaces.component";

@Component({
    selector: 'app-repository-details-bundle-file-entry-node',
    templateUrl: './repository-details-bundle-file-entry-node.component.html',
    styleUrls: ['./repository-details-bundle-file-entry-node.component.css']
})
export class RepositoryDetailsBundleFileEntryNodeComponent {

    @Input() public node: WorkspaceBundleFileEntryTreeNode;

    constructor() {
    }

}
