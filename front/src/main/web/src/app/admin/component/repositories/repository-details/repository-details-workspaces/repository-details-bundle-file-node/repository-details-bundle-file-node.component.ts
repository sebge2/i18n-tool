import {Component, Input} from '@angular/core';
import {WorkspaceBundleTreeNode} from "../repository-details-workspaces.component";
import {
    GitHubFileLink,
    GitHubLink
} from "../../../../../../core/shared/component/git-hub-link-button/git-hub-link-button.component";
import {RepositoryType} from "../../../../../../translations/model/repository/repository-type.model";
import {GitHubRepository} from "../../../../../../translations/model/repository/github-repository.model";

@Component({
    selector: 'app-repository-details-bundle-file-node',
    templateUrl: './repository-details-bundle-file-node.component.html',
    styleUrls: ['./repository-details-bundle-file-node.component.css']
})
export class RepositoryDetailsBundleFileNodeComponent {

    @Input() public node: WorkspaceBundleTreeNode;

    constructor() {
    }

    public get bundleLink(): GitHubLink {
        if (this.node && this.node.repository.type === RepositoryType.GITHUB) {
            const repository = <GitHubRepository>this.node.repository;

            return new GitHubFileLink(
                repository.username,
                repository.repository,
                this.node.workspace.branch,
                this.node.bundleFile.location
            );
        } else {
            return null;
        }
    }
}
