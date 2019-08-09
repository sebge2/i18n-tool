import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
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

    @Output()
    valueChange: EventEmitter<Workspace> = new EventEmitter<Workspace>();

    value: Promise<Workspace> = new Promise<Workspace>(() => {
    });

    workspaces: Observable<Workspace[]>;

    private destroy$ = new Subject();

    constructor(private workspaceService: WorkspaceService) {
    }

    ngOnInit() {
        setTimeout(
            () => {
                this.workspaces = this.workspaceService.getWorkspaces()
                    .pipe(
                        takeUntil(this.destroy$),
                        tap(
                            (workspaces: Workspace[]) => {
                                let currentWorkspace = workspaces.find(workspace => _.get(this.value, 'id') === workspace.id);

                                if (!this.value || !currentWorkspace) {
                                    currentWorkspace = workspaces.find(workspace => WorkspaceSelectorComponent.DEFAULT_BRANCH == workspace.branch);
                                }

                                this.value = Promise.resolve(currentWorkspace);
                                this.valueChange.emit(currentWorkspace);
                            }
                        )
                    );
            },
            0);
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    onChange(workspace: Workspace) {
        this.value = Promise.resolve(workspace);
        this.valueChange.emit(workspace);
    }

}
