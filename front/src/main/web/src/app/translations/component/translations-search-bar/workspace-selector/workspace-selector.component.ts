import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {FormControl} from "@angular/forms";
import {Workspace} from "../../../model/workspace.model";
import {WorkspaceService} from "../../../service/workspace.service";
import {Observable, Subject} from 'rxjs';
import {takeUntil, tap} from "rxjs/operators";
import * as _ from 'lodash';

@Component({
    selector: 'app-workspace-selector',
    templateUrl: './workspace-selector.component.html',
    styleUrls: ['./workspace-selector.component.css']
})
export class WorkspaceSelectorComponent implements OnInit, OnDestroy {

    private static DEFAULT_BRANCH = "master";

    workspaces: Observable<Workspace[]>;
    workspaceForm = new FormControl();

    @Output('selectedWorkspace')
    selectedWorkspace: EventEmitter<Workspace> = new EventEmitter<Workspace>();

    private destroy$ = new Subject();

    constructor(private workspaceService: WorkspaceService) {
    }

    ngOnInit() {
        this.workspaces = this.workspaceService.getWorkspaces()
            .pipe(
                takeUntil(this.destroy$),
                tap(
                    (workspaces: Workspace[]) => {
                        let currentWorkspace = workspaces.find(workspace => _.get(this.workspaceForm, 'value.id') === workspace.id);

                        if (!this.workspaceForm.value || !currentWorkspace) {
                            currentWorkspace = workspaces.find(workspace => WorkspaceSelectorComponent.DEFAULT_BRANCH == workspace.branch);
                        }

                        this.workspaceForm.setValue(currentWorkspace);
                        this.selectedWorkspace.emit(currentWorkspace);
                    }
                )
            );

        this.workspaceForm.valueChanges.subscribe((selectedWorkspace: Workspace) => this.selectedWorkspace.emit(selectedWorkspace));
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

}
