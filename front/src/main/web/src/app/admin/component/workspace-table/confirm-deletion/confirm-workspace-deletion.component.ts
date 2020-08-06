import {Component, Inject, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ValidatorFn} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {Workspace} from "../../../../translations/model/workspace.model";

@Component({
    selector: 'app-confirm-deletion',
    templateUrl: './confirm-workspace-deletion.component.html',
    styleUrls: ['./confirm-workspace-deletion.component.css']
})
export class ConfirmWorkspaceDeletionComponent implements OnInit {

    public form: FormGroup;
    public workspace: Workspace;

    constructor(private dialogRef: MatDialogRef<ConfirmWorkspaceDeletionComponent>,
                private formBuilder: FormBuilder,
                @Inject(MAT_DIALOG_DATA) public data: ConfirmDeletionDialogModel) {
        this.workspace = data.workspace;

        this.form = formBuilder.group(
            {
                name: this.formBuilder.control('', [expectedNameValidator(data.workspace.branch)])
            }
        );
    }

    ngOnInit() {
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    submit() {
        this.dialogRef.close({workspace: this.workspace});
    }
}

export interface ConfirmDeletionDialogModel {
    workspace: Workspace;
}

export function expectedNameValidator(expectedName: String): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
        return (expectedName != control.value) ? {'forbiddenName': {value: control.value}} : null;
    };
}
