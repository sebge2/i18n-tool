import {Component, Input} from '@angular/core';
import {WorkspaceBundleTreeNode} from "../repository-details-workspaces.component";
import {BundleType} from "../../../../../../translations/model/workspace/bundle-file.model";

@Component({
    selector: 'app-repository-details-bundle-file-node',
    templateUrl: './repository-details-bundle-file-node.component.html',
    styleUrls: ['./repository-details-bundle-file-node.component.css']
})
export class RepositoryDetailsBundleFileNodeComponent {

    @Input() public node: WorkspaceBundleTreeNode;

    constructor() {
    }

    public get fileTypeClass(): string {
        switch (this.node.bundleFile.type) {
            case BundleType.JAVA_PROPERTIES:
                return 'app-icon-java-file';
            case BundleType.JSON_ICU:
                return 'app-icon-json-file';
            default:
                return '';
        }
    }

    public get name(): string {
        switch (this.node.bundleFile.type) {
            case BundleType.JSON_ICU:
                return `${this.node.bundleFile.location}`;
            case BundleType.JAVA_PROPERTIES:
            default:
                return `${this.node.bundleFile.location}/${this.node.bundleFile.name}`;
        }
    }

}
