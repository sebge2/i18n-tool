import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from "@angular/forms";
import {Workspace} from "../../model/workspace.model";
import {WorkspaceStatus} from "../../model/workspace-status.model";
import {WorkspaceService} from "../../service/workspace.service";
import {Subscription} from 'rxjs';

@Component({
    selector: 'app-workspace-selector',
    templateUrl: './workspace-selector.component.html',
    styleUrls: ['./workspace-selector.component.css']
})
export class WorkspaceSelectorComponent implements OnInit, OnDestroy {

    private static DEFAULT_BRANCH = "master";

    private _workspaceForm = new FormControl();
    private _workspaces: Workspace[] = [];
    private _subscription: Subscription;

    constructor(private workspaceService: WorkspaceService) {
    }

    ngOnInit() {
        this.workspaceService.getWorkspaces().subscribe(
            (workpaces: Workspace[]) => {
                this._workspaces = workpaces;

                if ((this._workspaceForm.value == null) || (workpaces.find(workspace => this._workspaceForm.value.id == workspace.id) != null)) {
                    this.workspaceForm.setValue(
                        this.workspaces
                            .find(workspace => WorkspaceSelectorComponent.DEFAULT_BRANCH == workspace.branch)
                    );
                }
            }
        );
    }

    ngOnDestroy(): void {
        this._subscription.unsubscribe();
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
