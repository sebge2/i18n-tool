import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '@i18n-core-auth';
import { NotificationService } from '@i18n-core-notification';

@Component({
  selector: 'app-edit-password',
  templateUrl: './edit-password.component.html',
  styleUrls: ['./edit-password.component.css'],
})
export class EditPasswordComponent {
  readonly form: FormGroup;
  loading = false;

  constructor(
    private _formBuilder: FormBuilder,
    private _userService: UserService,
    private _notificationService: NotificationService
  ) {
    this.form = this._formBuilder.group(
      {
        currentPassword: this._formBuilder.control('', [Validators.required]),
        newPassword: this._formBuilder.control('', [Validators.minLength(6), Validators.required]),
        confirmedPassword: this._formBuilder.control('', []),
      },
      {
        validator: this._mustMatch('newPassword', 'confirmedPassword'),
      }
    );
  }

  onSave() {
    this.loading = true;

    this._userService
      .updateCurrentUserPassword({
        currentPassword: this.form.controls['currentPassword'].value,
        newPassword: this.form.controls['newPassword'].value,
      })
      .toPromise()
      .then((_) => this._makeFormUntouched())
      .catch((error) => this._notificationService.displayErrorMessage('ACCOUNT.ERROR.SAVE_PASSWORD', error))
      .finally(() => (this.loading = false));
  }

  resetForm() {
    this.form.controls['currentPassword'].setValue(null);
    this.form.controls['newPassword'].setValue(null);
    this.form.controls['confirmedPassword'].setValue(null);

    this._makeFormUntouched();
  }

  private _makeFormUntouched() {
    this.form.markAsPristine();
    this.form.markAsUntouched();
  }

  private _mustMatch(controlName: string, matchingControlName: string) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];

      if (matchingControl.errors && !matchingControl.errors.mustMatch) {
        // return if another validator has already found an error on the matchingControl
        return;
      }

      // set error on matchingControl if validation fails
      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ mustMatch: true });
      } else {
        matchingControl.setErrors(null);
      }
    };
  }
}
