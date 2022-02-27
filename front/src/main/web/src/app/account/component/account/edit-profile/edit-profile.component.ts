import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthenticationService } from '@i18n-core-auth';
import { User } from '@i18n-core-auth';
import { Subject } from 'rxjs';
import { UserService } from '@i18n-core-auth';
import { NotificationService } from '@i18n-core-notification';
import { ImportedFile } from '@i18n-core-shared';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css'],
})
export class EditProfileComponent implements OnInit, OnDestroy {
  readonly form: FormGroup;
  currentUser: User;

  cancelInProgress: boolean = false;
  saveInProgress: boolean = false;

  private readonly _destroyed$ = new Subject<void>();

  constructor(
    private _formBuilder: FormBuilder,
    private _authenticationService: AuthenticationService,
    private _notificationService: NotificationService,
    private _userService: UserService
  ) {
    this.form = this._formBuilder.group({
      username: this._formBuilder.control('', [Validators.required]),
      displayName: this._formBuilder.control('', [Validators.required]),
      email: this._formBuilder.control('', [Validators.required, Validators.email]),
      avatar: this._formBuilder.control(null, []),
    });
  }

  ngOnInit() {
    this._authenticationService.currentUser().subscribe((currentUser) => {
      this.currentUser = currentUser;
      this.resetForm();
    });
  }

  ngOnDestroy(): void {
    this._destroyed$.next(null);
    this._destroyed$.complete();
  }

  onSave() {
    this.saveInProgress = true;

    this._saveAvatar()
      .then((_) => this._saveProfile())
      .then((_) => this._makeFormUntouched())
      .catch((error) => this._notificationService.displayErrorMessage('ACCOUNT.ERROR.SAVE_PROFILE', error))
      .finally(() => (this.saveInProgress = false));
  }

  resetForm() {
    this.cancelInProgress = true;

    this.form.controls['username'].setValue(this.currentUser.username);
    this.form.controls['displayName'].setValue(this.currentUser.displayName);
    this.form.controls['email'].setValue(this.currentUser.email);
    this.form.controls['avatar'].setValue(null);

    if (this._isEditionNotAllowed()) {
      this.form.disable();
    }

    this._makeFormUntouched();
    this.cancelInProgress = false;
  }

  get actionInProgress(): boolean {
    return this.cancelInProgress || this.saveInProgress;
  }

  private _makeFormUntouched() {
    this.form.markAsPristine();
    this.form.markAsUntouched();
  }

  private _isEditionNotAllowed() {
    return this.currentUser.isAdminUser() || this.currentUser.isExternal();
  }

  private _saveAvatar(): Promise<any> {
    if (!this.form.controls['avatar'].pristine) {
      return this._userService.updateCurrentUserAvatar(<ImportedFile>this.form.controls['avatar'].value).toPromise();
    } else {
      return Promise.resolve();
    }
  }

  private _saveProfile(): Promise<any> {
    return this._userService
      .updateCurrentUser({
        displayName: this.form.controls['displayName'].value,
        username: this.form.controls['username'].value,
        email: this.form.controls['email'].value,
      })
      .toPromise();
  }
}
