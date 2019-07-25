import {Component, OnInit} from '@angular/core';
import {FormControl} from "@angular/forms";
import {Workspace} from "../../model/workspace.model";
import {WorkspaceStatus} from "../../model/workspace-status.model";

@Component({
    selector: 'app-workspace-selector',
    templateUrl: './workspace-selector.component.html',
    styleUrls: ['./workspace-selector.component.css']
})
export class WorkspaceSelectorComponent implements OnInit {

    private static DEFAULT_BRANCH = "master";

    private _workspaceForm = new FormControl();
    private _workspaces: Workspace[];

    constructor() {
        this._workspaces = [
            new Workspace(<Workspace>{'branch': 'master', 'status': WorkspaceStatus.INITIALIZED}),
            new Workspace(<Workspace>{'branch': 'release/2019.6', 'status': WorkspaceStatus.IN_REVIEW}),
            new Workspace(<Workspace>{'branch': 'release/2019.5', 'status': WorkspaceStatus.NOT_INITIALIZED}),
            new Workspace(<Workspace>{'branch': 'release/2019.4', 'status': WorkspaceStatus.NOT_INITIALIZED}),
        ];
    }

    ngOnInit() {
        let defaultWorkspace = this.workspaces
            .find(workspace => WorkspaceSelectorComponent.DEFAULT_BRANCH == workspace.branch);

        this.workspaceForm.setValue(defaultWorkspace);
    }

    get workspaceForm(): FormControl {
        return this._workspaceForm;
    }

    get workspaces(): Workspace[] {
        return this._workspaces;
    }

    getCssStatus(workspace: Workspace): String {
        switch (workspace.status) {
            case WorkspaceStatus.IN_REVIEW:
                return "icon-in-review";
            case WorkspaceStatus.INITIALIZED:
                return "icon-initialized";
            case WorkspaceStatus.NOT_INITIALIZED:
                return "icon-not-initialized";
            default:
                return "";
        }
    }

    getIcon(workspace: Workspace): String {
        switch (workspace.status) {
            case WorkspaceStatus.IN_REVIEW:
                return "lock";
            case WorkspaceStatus.INITIALIZED:
                return "check_circle";
            case WorkspaceStatus.NOT_INITIALIZED:
                return "warning";

        }
    }
}
