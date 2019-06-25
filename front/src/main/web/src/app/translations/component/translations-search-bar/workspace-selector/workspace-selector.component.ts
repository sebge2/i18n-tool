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

    private lastValue: Workspace;

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
                                let currentWorkspace = workspaces.find(workspace => _.get(this.lastValue, 'id') === workspace.id);

                                if (!this.value || !currentWorkspace) {
                                    currentWorkspace = workspaces.find(workspace => WorkspaceSelectorComponent.DEFAULT_BRANCH == workspace.branch);
                                }

                                this.lastValue = currentWorkspace;
                                this.value = Promise.resolve(this.lastValue);
                                this.valueChange.emit(this.lastValue);
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
        this.lastValue = workspace;

        this.value = Promise.resolve(this.lastValue);
        this.valueChange.emit(this.lastValue);
    }

}
