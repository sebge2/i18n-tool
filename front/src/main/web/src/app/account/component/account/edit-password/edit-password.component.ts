import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
    selector: 'app-edit-password',
    templateUrl: './edit-password.component.html',
    styleUrls: ['./edit-password.component.css']
})
export class EditPasswordComponent implements OnInit {

    public readonly form: FormGroup;
    public loading = false;

    constructor(private formBuilder: FormBuilder) {
        this.form = this.formBuilder.group(
            {
                currentPassword: this.formBuilder.control('', [Validators.required]),
                newPassword: this.formBuilder.control('', [Validators.minLength(6), Validators.required]),
                confirmedPassword: this.formBuilder.control('', [])
            }, {
                validator: this.mustMatch('newPassword', 'confirmedPassword')
            }
        );
    }

    public ngOnInit() {
    }

    public onSave() {

    }

    public resetForm() {
        this.form.controls['currentPassword'].setValue(null);
        this.form.controls['newPassword'].setValue(null);
        this.form.controls['confirmedPassword'].setValue(null);

        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

    private mustMatch(controlName: string, matchingControlName: string) {
        return (formGroup: FormGroup) => {
            const control = formGroup.controls[controlName];
            const matchingControl = formGroup.controls[matchingControlName];

            if (matchingControl.errors && !matchingControl.errors.mustMatch) {
                // return if another validator has already found an error on the matchingControl
                return;
            }

            // set error on matchingControl if validation fails
            if (control.value !== matchingControl.value) {
                matchingControl.setErrors({mustMatch: true});
            } else {
                matchingControl.setErrors(null);
            }
        }
    }
}
