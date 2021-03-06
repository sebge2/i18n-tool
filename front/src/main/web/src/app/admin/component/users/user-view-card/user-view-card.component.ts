import {Component, EventEmitter, Input, Output} from '@angular/core';
import {User} from "../../../../core/auth/model/user.model";
import {AbstractControl, FormBuilder, FormGroup, Validators} from "@angular/forms";
import * as _ from "lodash";
import {UserRole} from "../../../../core/auth/model/user-role.model";
import {UserService} from "../../../../core/auth/service/user.service";
import {NotificationService} from "../../../../core/notification/service/notification.service";
import {InternalUserCreationDto, UserPatchDto} from "../../../../api";
import {getStringValue} from "../../../../core/shared/utils/form-utils";

export class RoleOption {

    constructor(public admin: boolean,
                public label: string,
                public icon: string) {
    }
}

@Component({
    selector: 'app-user-view-card',
    templateUrl: './user-view-card.component.html',
    styleUrls: ['./user-view-card.component.css']
})
export class UserViewCardComponent {

    @Output() public save = new EventEmitter<User>();
    @Output() public delete = new EventEmitter<User>();

    public readonly form: FormGroup;
    public readonly roleOptionNoPrivilege = new RoleOption(false, 'ADMIN.USERS.ROLES.NO_PRIVILEGE', 'perm_identity');
    public readonly roleOptionAdminPrivilege = new RoleOption(true, 'ADMIN.USERS.ROLES.ADMIN_PRIVILEGE', 'admin_panel_settings');
    public readonly roleOptions = [this.roleOptionNoPrivilege, this.roleOptionAdminPrivilege];

    public cancelInProgress: boolean = false;
    public deleteInProgress: boolean = false;
    public saveInProgress: boolean = false;

    private _user: User;

    constructor(private formBuilder: FormBuilder,
                private userService: UserService,
                private notificationService: NotificationService) {
        this.form = this.formBuilder.group(
            {
                username: this.formBuilder.control('', [Validators.required]),
                displayName: this.formBuilder.control('', [Validators.required]),
                email: this.formBuilder.control('', [Validators.required, Validators.email]),
                adminRole: this.formBuilder.control(false, []),
                password: this.formBuilder.control(false, [])
            }
        );
    }

    @Input()
    public get user(): User {
        return this._user;
    }

    public set user(user: User) {
        this._user = user;

        this.resetForm();
    }

    public get displayNameForm(): AbstractControl {
        return this.form.controls['displayName'];
    }

    public get displayName(): string {
        const displayName = getStringValue(this.displayNameForm);

        return !_.isEmpty(displayName) ? displayName : "-";
    }

    public get externalAuthProvider(): string {
        return this.user.isExternal() ? this.user.externalAuthSystem : 'ADMIN.USERS.USER_TYPE.INTERNAL';
    }

    public get usernameForm(): AbstractControl {
        return this.form.controls['username'];
    }

    public get username(): string {
        return getStringValue(this.usernameForm);
    }

    public get emailForm(): AbstractControl {
        return this.form.controls['email'];
    }

    public get email(): string {
        return getStringValue(this.emailForm);
    }

    public get passwordForm(): AbstractControl {
        return this.form.controls['password'];
    }

    public get password(): string {
        return getStringValue(this.passwordForm);
    }

    public get rolesForm(): AbstractControl {
        return this.form.controls['adminRole'];
    }

    public get roles(): UserRole[] {
        if (this.rolesForm.value === this.roleOptionAdminPrivilege) {
            return [UserRole.ADMIN]
        } else {
            return [];
        }
    }

    public get avatarUrl(): string {
        return (this.user != null) && this.isExistingUser()
            ? `/api/user/${this.user.id}/avatar`
            : null;
    }

    public get deleteAllowed(): boolean {
        return !this.user.isAdminUser();
    }

    public get isMoreActionsEnabled(): boolean {
        return this.isExistingUser() && this.user.isInternal()
    }

    public get isPasswordEditionAllowed(): boolean {
        return this.user.isInternal() && !this.isExistingUser();
    }

    public get actionInProgress(): boolean {
        return this.cancelInProgress || this.saveInProgress || this.deleteInProgress;
    }

    public onCancel() {
        this.cancelInProgress = true;
        this.resetForm();
        this.cancelInProgress = false;
    }

    public onSave() {
        this.saveInProgress = true;

        if (this.isExistingUser()) {
            this.userService
                .updateUser(this.user.id, this.toUpdatedUser())
                .toPromise()
                .then(user => this.user = user)
                .then(user => this.save.emit(user))
                .catch(error => this.notificationService.displayErrorMessage('ADMIN.USERS.ERROR.UPDATE', error))
                .finally(() => this.saveInProgress = false);
        } else {
            this.userService
                .createUser(this.toNewUser())
                .toPromise()
                .then(user => this.user = user)
                .then(user => this.save.emit(user))
                .catch(error => this.notificationService.displayErrorMessage('ADMIN.USERS.ERROR.SAVE', error))
                .finally(() => this.saveInProgress = false);
        }
    }

    public onDelete() {
        if (this.user.id) {
            this.deleteInProgress = true;
            this.userService
                .deleteUser(this.user)
                .toPromise()
                .catch(error => this.notificationService.displayErrorMessage('ADMIN.USERS.ERROR.DELETE', error))
                .finally(() => this.deleteInProgress = false);
        } else {
            this.delete.emit();
        }
    }

    public onGeneratedPassword(generatedPassword: string) {
        this.passwordForm.setValue(generatedPassword);
    }

    public isExistingUser(): boolean {
        return !!this.user.id;
    }

    private toNewUser(): InternalUserCreationDto {
        return {
            username: this.username,
            displayName: this.displayName,
            email: this.email,
            password: this.password,
            roles: this.roles
        };
    }

    private toUpdatedUser(): UserPatchDto {
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

    private resetForm() {
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
            _.some(this.user.roles, role => role === UserRole.ADMIN) ? this.roleOptionAdminPrivilege : this.roleOptionNoPrivilege
        );

        this.rolesForm.markAsPristine();

        if (this.user.isAdminUser()) {
            this.form.disable()
        }

        // this.form.markAsPristine();
        // this.form.markAsUntouched();
    }
}
