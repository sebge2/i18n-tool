import {Component, Input} from '@angular/core';

export class GitHubLink {

    constructor(public userName: string,
                public repository?: string,
                public branchName?: string,
                public filePath?: string) {
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
        let url = `https://github.com/${this.link.userName}`;

        if (this.link.repository) {
            url += `/${this.link.repository}`;

            if (this.link.branchName) {
                url += `/tree/${this.link.branchName}`;

                if (this.link.filePath) {
                    url += `/${this.link.filePath}`;
                }
            }
        }

        window.open(url, "_blank");
    }
}
