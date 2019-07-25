import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from "@angular/forms";
import {Workspace} from "../../model/workspace.model";
import {WorkspaceStatus} from "../../model/workspace-status.model";
import {WorkspaceService} from "../../service/workspace.service";
import {Observable, Subject, Subscription} from 'rxjs';
import {takeUntil, tap} from "rxjs/operators";

@Component({
    selector: 'app-workspace-selector',
    templateUrl: './workspace-selector.component.html',
    styleUrls: ['./workspace-selector.component.css']
})
export class WorkspaceSelectorComponent implements OnInit, OnDestroy {

    private static DEFAULT_BRANCH = "master";

    workspaces: Observable<Workspace[]>; // Observable<Observable<Workspace>[]> TODO
    workspaceForm = new FormControl(); // todo reactive

    private destroy$ = new Subject();

    constructor(private workspaceService: WorkspaceService) {
    }

    ngOnInit() {
        this.workspaces = this.workspaceService.getWorkspaces()
            .pipe(
                takeUntil(this.destroy$),
                tap(
                    (workspaces: Workspace[]) => {
                        if ((this.workspaceForm.value == null) || (workspaces.find(workspace => this.workspaceForm.value.id == workspace.id) != null)) {
                            this.workspaceForm.setValue(
                                workspaces
                                    .find(workspace => WorkspaceSelectorComponent.DEFAULT_BRANCH == workspace.branch)
                            );
                        }
                    }
                )
            );
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

}
