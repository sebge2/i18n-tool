import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
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

    @Input()
    value: Workspace;

    workspaces: Observable<Workspace[]>;

    private destroy$ = new Subject();

    constructor(private workspaceService: WorkspaceService) {
    }

    ngOnInit() {
        this.workspaces = this.workspaceService.getWorkspaces()
            .pipe(
                takeUntil(this.destroy$),
                tap(
                    (workspaces: Workspace[]) => {
                        let currentWorkspace = workspaces.find(workspace => _.get(this.value, 'id') === workspace.id);

                        if (!this.value || !currentWorkspace) {
                            currentWorkspace = workspaces.find(workspace => WorkspaceSelectorComponent.DEFAULT_BRANCH == workspace.branch);
                        }

                        this.value = currentWorkspace;
                        this.valueChange.emit(this.value);
                    }
                )
            );
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    onChange(workspace: Workspace){
        this.value = workspace;
        this.valueChange.emit(this.value);
    }

}
