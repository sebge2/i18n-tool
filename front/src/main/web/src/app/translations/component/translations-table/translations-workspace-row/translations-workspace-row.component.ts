import {Component, Input} from '@angular/core';
import {Workspace} from "../../../model/workspace/workspace.model";

@Component({
    selector: 'app-translations-workspace-row',
    templateUrl: './translations-workspace-row.component.html',
    styleUrls: ['./translations-workspace-row.component.css']
})
export class TranslationsWorkspaceRowComponent {

    @Input() public workspace: Workspace;

    constructor() {
    }
}
