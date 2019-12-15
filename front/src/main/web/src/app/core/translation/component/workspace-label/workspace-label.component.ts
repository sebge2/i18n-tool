import {Component, Input} from '@angular/core';
import {Workspace} from "../../../../translations/model/workspace/workspace.model";

@Component({
    selector: 'app-workspace-label',
    templateUrl: './workspace-label.component.html',
    styleUrls: ['./workspace-label.component.css']
})
export class WorkspaceLabelComponent {

    @Input() public workspace: Workspace;

    constructor() {
    }

}
