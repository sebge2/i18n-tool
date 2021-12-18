import { Component, EventEmitter, Input, Output } from '@angular/core';
import { User } from '@i18n-core-auth';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import * as _ from 'lodash';
import { UserRole } from '@i18n-core-auth';
import { UserService } from '@i18n-core-auth';
import { NotificationService } from '@i18n-core-notification';
import { InternalUserCreationDto, UserPatchDto } from '../../../../api';
import {getStringValue, TranslationKey} from '@i18n-core-shared';

export class RoleOption {
  constructor(public admin: boolean, public label: string, public icon: string) {}
}

@Component({
  selector: 'app-user-view-card',
  templateUrl: './user-view-card.component.html',
  styleUrls: ['./user-view-card.component.css'],
})
export class UserViewCardComponent {
  @Output() save = new EventEmitter<User>();
  @Output() delete = new EventEmitter<User>();

  readonly form: FormGroup;
  readonly roleOptionNoPrivilege = new RoleOption(false, 'ADMIN.USERS.ROLES.NO_PRIVILEGE', 'perm_identity');
  readonly roleOptionAdminPrivilege = new RoleOption(true, 'ADMIN.USERS.ROLES.ADMIN_PRIVILEGE', 'admin_panel_settings');
  readonly roleOptions = [this.roleOptionNoPrivilege, this.roleOptionAdminPrivilege];

  cancelInProgress: boolean = false;
  deleteInProgress: boolean = false;
  saveInProgress: boolean = false;

  private _user: User;

  constructor(
    private _formBuilder: FormBuilder,
    private _userService: UserService,
    private _notificationService: NotificationService
  ) {
    this.form = this._formBuilder.group({
      username: this._formBuilder.control('', [Validators.required]),
      displayName: this._formBuilder.control('', [Validators.required]),
      email: this._formBuilder.control('', [Validators.required, Validators.email]),
      adminRole: this._formBuilder.control(false, []),
      password: this._formBuilder.control(false, []),
    });
  }

  @Input()
  get user(): User {
    return this._user;
  }

  set user(user: User) {
    this._user = user;

    this._resetForm();
  }

  get displayNameForm(): AbstractControl {
    return this.form.controls['displayName'];
  }

  get displayName(): string {
    const displayName = getStringValue(this.displayNameForm);

    return !_.isEmpty(displayName) ? displayName : '-';
  }

  get externalAuthProvider(): string {
    return this.user.isExternal() ? this.user.externalAuthSystem : 'ADMIN.USERS.USER_TYPE.INTERNAL';
  }

  get usernameForm(): AbstractControl {
    return this.form.controls['username'];
  }

  get username(): string {
    return getStringValue(this.usernameForm);
  }

  get emailForm(): AbstractControl {
    return this.form.controls['email'];
  }

  get email(): string {
    return getStringValue(this.emailForm);
  }

  get passwordForm(): AbstractControl {
    return this.form.controls['password'];
  }

  get password(): string {
    return getStringValue(this.passwordForm);
  }

  get rolesForm(): AbstractControl {
    return this.form.controls['adminRole'];
  }

  get roles(): UserRole[] {
    if (this.rolesForm.value === this.roleOptionAdminPrivilege) {
      return [UserRole.ADMIN];
    } else {
      return [];
    }
  }

  get avatarUrl(): string {
    return this.user != null && this.isExistingUser() ? `/api/user/${this.user.id}/avatar` : null;
  }

  get deleteAllowed(): boolean {
    return !this.user.isAdminUser();
  }

  get isMoreActionsEnabled(): boolean {
    return this.isExistingUser() && this.user.isInternal();
  }

  get isPasswordEditionAllowed(): boolean {
    return this.user.isInternal() && !this.isExistingUser();
  }

  get actionInProgress(): boolean {
    return this.cancelInProgress || this.saveInProgress || this.deleteInProgress;
  }

  onCancel() {
    this.cancelInProgress = true;
    this._resetForm();
    this.cancelInProgress = false;
  }

  onSave() {
    this.saveInProgress = true;

    if (this.isExistingUser()) {
      this._userService
        .updateUser(this.user.id, this._toUpdatedUser())
        .toPromise()
        .then((user) => (this.user = user))
        .then((user) => this.save.emit(user))
        .catch((error) => this._notificationService.displayErrorMessage('ADMIN.USERS.ERROR.UPDATE', error))
        .finally(() => (this.saveInProgress = false));
    } else {
      this._userService
        .createUser(this._toNewUser())
        .toPromise()
        .then((user) => (this.user = user))
        .then((user) => this.save.emit(user))
        .catch((error) => this._notificationService.displayErrorMessage('ADMIN.USERS.ERROR.SAVE', error))
        .finally(() => (this.saveInProgress = false));
    }
  }

  onDelete() {
    if (this.user.id) {
      this.deleteInProgress = true;
      this._userService
        .deleteUser(this.user)
        .toPromise()
        .catch((error) => this._notificationService.displayErrorMessage('ADMIN.USERS.ERROR.DELETE', error))
        .finally(() => (this.deleteInProgress = false));
    } else {
      this.delete.emit();
    }
  }

  onGeneratedPassword(generatedPassword: string) {
    this.passwordForm.setValue(generatedPassword);

    this._notificationService.displayInfoMessage(new TranslationKey('ADMIN.USERS.PASSWORD_COPIED'));
  }

  isExistingUser(): boolean {
    return !!this.user.id;
  }

  private _toNewUser(): InternalUserCreationDto {
    return {
      username: this.username,
      displayName: this.displayName,
      email: this.email,
      password: this.password,
      roles: this.roles,
    };
  }

  private _toUpdatedUser(): UserPatchDto {
    const patch: UserPatchDto = {};

    if (this.usernameForm.dirty) {
      patch.username = this.username;
    }

    if (this.displayNameForm.dirty) {
      patch.displayName = this.displayName;
    }

    if (this.emailForm.dirty) {
      patch.email = this.email;
    }

    if (this.passwordForm.dirty) {
      patch.password = this.password;
    }

    if (this.rolesForm.dirty) {
      patch.roles = this.roles;
    }

    return patch;
  }

  private _resetForm() {
    this.usernameForm.setValue(this.user.username);
    if (this.user.isExternal()) {
      this.usernameForm.disable();
    }
    this.usernameForm.markAsPristine();

    this.displayNameForm.setValue(this.user.displayName);
    if (this.user.isExternal()) {
      this.displayNameForm.disable();
    }
    this.displayNameForm.markAsPristine();

    this.emailForm.setValue(this.user.email);
    if (this.user.isExternal()) {
      this.emailForm.disable();
    }
    this.emailForm.markAsPristine();

    if (this.isPasswordEditionAllowed) {
      this.passwordForm.setValue(null);

      this.passwordForm.setValidators([Validators.minLength(6), Validators.required]);
    } else {
      this.passwordForm.disable();
    }

    this.passwordForm.markAsPristine();

    this.rolesForm.setValue(
      _.some(this.user.roles, (role) => role === UserRole.ADMIN)
        ? this.roleOptionAdminPrivilege
        : this.roleOptionNoPrivilege
    );

    this.rolesForm.markAsPristine();

    if (this.user.isAdminUser()) {
      this.form.disable();
    }

    // this.form.markAsPristine();
    // this.form.markAsUntouched();
  }
}
