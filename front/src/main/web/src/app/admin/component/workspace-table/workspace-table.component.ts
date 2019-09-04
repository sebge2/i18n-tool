import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatDialog, MatTableDataSource} from '@angular/material';
import {Workspace} from "../../../translations/model/workspace.model";
import {WorkspaceService} from "../../../translations/service/workspace.service";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {
    ConfirmDeletionDialogModel,
    ConfirmWorkspaceDeletionComponent
} from "./confirm-deletion/confirm-workspace-deletion.component";

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

    constructor(private workspaceService: WorkspaceService,
                private dialog: MatDialog) {
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
        this.dialog
            .open(ConfirmWorkspaceDeletionComponent, {
                width: '400px',
                data: <ConfirmDeletionDialogModel>{workspace: workspace}
            })
            .afterClosed()
            .subscribe((result: ConfirmDeletionDialogModel) => {
                if (result) {
                    // this.startReviewing = true; TODO

                    this.workspaceService
                        .delete(result.workspace)
                        .finally(() => {
                            // this.startReviewing = false; TODO
                        });
                }
            });
    }

}
