import {Component, Inject} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
    selector: 'app-workspaces-start-review-dialog',
    templateUrl: './workspaces-start-review-dialog.component.html',
    styleUrls: ['./workspaces-start-review-dialog.component.css']
})
export class WorkspacesStartReviewDialogComponent {

    public readonly form: FormGroup;
    public publishInProgress: boolean = false;

    constructor(private dialogRef: MatDialogRef<WorkspacesStartReviewDialogComponent>,
                private formBuilder: FormBuilder,
                @Inject(MAT_DIALOG_DATA) public data: any) {
        this.form = formBuilder.group({
            comment: ['', Validators.required],
            workspaces: [[], Validators.required],
        });
    }

    public onPublish() {
        console.log(this.form.controls['workspaces'].value);
        console.log(this.form.controls['comment'].value);
    }
}
