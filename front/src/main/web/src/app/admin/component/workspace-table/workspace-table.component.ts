import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatTableDataSource} from '@angular/material';
import {Workspace} from "../../../translations/model/workspace.model";
import {WorkspaceService} from "../../../translations/service/workspace.service";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";

@Component({
    selector: 'app-workspace-table',
    templateUrl: './workspace-table.component.html',
    styleUrls: ['./workspace-table.component.css']
})
export class WorkspaceTableComponent implements OnInit, OnDestroy {

    displayedColumns = ['name', 'action'];

    hoveredWorkspace: Workspace;

    dataSource = new MatTableDataSource<Workspace>([]);

    private destroy$ = new Subject();

    constructor(private workspaceService: WorkspaceService) {
    }

    ngOnInit(): void {
        this.workspaceService.getWorkspaces()
            .pipe(takeUntil(this.destroy$))
            .subscribe((workspaces: Workspace[]) => {
                this.dataSource.data = workspaces;
            });
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    delete(workspace: Workspace): void {
        alert(workspace.branch);
    }

}
