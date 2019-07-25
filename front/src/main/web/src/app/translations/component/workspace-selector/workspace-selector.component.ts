import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from "@angular/forms";
import {Workspace} from "../../model/workspace.model";
import {WorkspaceService} from "../../service/workspace.service";
import {Observable, Subject} from 'rxjs';
import {takeUntil, tap} from "rxjs/operators";

@Component({
    selector: 'app-workspace-selector',
    templateUrl: './workspace-selector.component.html',
    styleUrls: ['./workspace-selector.component.css']
})
export class WorkspaceSelectorComponent implements OnInit, OnDestroy {

    private static DEFAULT_BRANCH = "master";

    workspaces: Observable<Workspace[]>;
    workspaceForm = new FormControl();

    private destroy$ = new Subject();

    constructor(private workspaceService: WorkspaceService) {
    }

    ngOnInit() {
        this.workspaces = this.workspaceService.getWorkspaces()
            .pipe(
                takeUntil(this.destroy$),
                tap(
                    (workspaces: Workspace[]) => {
                        if (!this.workspaceForm.value || !workspaces.find(workspace => this.workspaceForm.value.id == workspace.id)) {
                            const defaultWorkspace = workspaces.find(workspace => WorkspaceSelectorComponent.DEFAULT_BRANCH == workspace.branch);

                            this.workspaceForm.setValue(defaultWorkspace);
                        }
                    }
                )
            );

        this.workspaceForm.valueChanges.subscribe((selectedWorkspace: Workspace) => console.log(selectedWorkspace))
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

}
