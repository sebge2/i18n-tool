import {Component, Input} from '@angular/core';
import {EnrichedWorkspace} from "../../../model/workspace/enriched-workspace.model";

@Component({
    selector: 'app-translations-workspace-row',
    templateUrl: './translations-workspace-row.component.html',
    styleUrls: ['./translations-workspace-row.component.css']
})
export class TranslationsWorkspaceRowComponent {

    @Input() public workspace: EnrichedWorkspace;

    constructor() {
    }
}
