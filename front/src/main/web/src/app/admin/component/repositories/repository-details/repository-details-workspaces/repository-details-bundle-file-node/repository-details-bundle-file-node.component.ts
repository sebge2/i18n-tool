import {Component, Input, OnInit} from '@angular/core';
import {WorkspaceBundleTreeNode} from "../repository-details-workspaces.component";
import {BundleType} from "../../../../../../translations/model/workspace/bundle-file.model";
import {GitHubLink} from "../../../../../../core/shared/component/git-hub-link-button/git-hub-link-button.component";
import {RepositoryType} from "../../../../../../translations/model/repository/repository-type.model";
import {GitHubRepository} from "../../../../../../translations/model/repository/github-repository.model";

@Component({
    selector: 'app-repository-details-bundle-file-node',
    templateUrl: './repository-details-bundle-file-node.component.html',
    styleUrls: ['./repository-details-bundle-file-node.component.css']
})
export class RepositoryDetailsBundleFileNodeComponent implements OnInit {

    @Input() public node: WorkspaceBundleTreeNode;

    public bundleLink: GitHubLink;

    constructor() {
    }

    public ngOnInit(): void {
        if (this.gitHubLinkAvailable) {
            const repository = <GitHubRepository>this.node.repository;

            this.bundleLink = new GitHubLink(
                repository.username,
                repository.repository,
                this.node.workspace.branch,
                this.node.bundleFile.location
            );
        }
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

    public get gitHubLinkAvailable(): boolean {
        return this.node && this.node.repository.type === RepositoryType.GITHUB;
    }
}
