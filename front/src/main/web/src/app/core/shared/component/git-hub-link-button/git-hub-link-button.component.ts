import {Component, Input} from '@angular/core';

export interface GitHubLink {

    getUrl(): string;
}

export class GitHubFileLink implements GitHubLink {

    constructor(public userName: string,
                public repository?: string,
                public branchName?: string,
                public filePath?: string) {
    }

    getUrl(): string {
        let url = `https://github.com/${this.userName}`;

        if (this.repository) {
            url += `/${this.repository}`;

            if (this.branchName) {
                url += `/tree/${this.branchName}`;

                if (this.filePath) {
                    url += `/${this.filePath}`;
                }
            }
        }

        return url;
    }
}

export class GitHubPRLink implements GitHubLink {

    constructor(public userName: string,
                public repository: string,
                public number: number) {
    }

    getUrl(): string {
        return `https://github.com/${this.userName}/${this.repository}/pull/${this.number}`;
    }
}

@Component({
    selector: 'app-git-hub-link-button',
    templateUrl: './git-hub-link-button.component.html',
    styleUrls: ['./git-hub-link-button.component.css']
})
export class GitHubLinkButtonComponent {

    @Input() public disabled: boolean = false;
    @Input() public link: GitHubLink;

    constructor() {
    }

    public onClick() {
        window.open(this.link.getUrl(), "_blank");
    }
}
