import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Repository} from "../../../../translations/model/repository/repository.model";
import {Workspace} from "../../../../translations/model/workspace/workspace.model";
import {WorkspaceService} from "../../../../translations/service/workspace.service";
import {Subject} from "rxjs";

@Component({
    selector: 'app-workspaces-start-review-dialog',
    templateUrl: './workspaces-start-review-dialog.component.html',
    styleUrls: ['./workspaces-start-review-dialog.component.css']
})
export class WorkspacesStartReviewDialogComponent implements OnInit, OnDestroy {

    public readonly form: FormGroup;
    public publishInProgress: boolean = false;
    public workspaces: Workspace[] = [];

    private _destroyed$ = new Subject<void>();

    constructor(private dialogRef: MatDialogRef<WorkspacesStartReviewDialogComponent>,
                private formBuilder: FormBuilder,
                @Inject(MAT_DIALOG_DATA) public data: { repository: Repository },
                private _workspaceService: WorkspaceService) {
        this.form = formBuilder.group({
            comment: ['', Validators.required],
            workspaces: [[], Validators.required],
        });
    }

    public ngOnInit(): void {
        this._workspaceService.getWorkspaces()
            // TODO
            .subscribe(workspaces => this.workspaces = workspaces);
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public onPublish() {
        console.log(this.form.controls['workspaces'].value);
        console.log(this.form.controls['comment'].value);
    }
}
