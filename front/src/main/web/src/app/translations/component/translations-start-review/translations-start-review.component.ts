import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
    selector: 'app-translations-start-review',
    templateUrl: './translations-start-review.component.html',
    styleUrls: ['./translations-start-review.component.css']
})
export class TranslationsStartReviewComponent implements OnInit {

    form: FormGroup;

    constructor(private dialogRef: MatDialogRef<TranslationsStartReviewComponent>,
                private formBuilder: FormBuilder,
                @Inject(MAT_DIALOG_DATA) public data: StartReviewDialogModel) {
        this.form = formBuilder.group(
            {
                comment: this.formBuilder.control('', [Validators.required])
            }
        );
    }

    ngOnInit() {
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    submit() {
        this.dialogRef.close({comment: this.form.value.comment});
    }
}

export interface StartReviewDialogModel {
    comment: string;
}
