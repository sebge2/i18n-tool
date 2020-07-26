import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {User} from "../../../../core/auth/model/user.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import * as _ from "lodash";
import {UserRole} from "../../../../core/auth/model/user-role.model";

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
export class UserViewCardComponent implements OnInit {

    @Output() public save = new EventEmitter<User>();

    public readonly form: FormGroup;
    public readonly roleOptionNoPrivilege = new RoleOption(false, 'ADMIN.USERS.ROLES.NO_PRIVILEGE', 'perm_identity');
    public readonly roleOptionAdminPrivilege = new RoleOption(true, 'ADMIN.USERS.ROLES.ADMIN_PRIVILEGE', 'admin_panel_settings');
    public readonly roleOptions = [this.roleOptionNoPrivilege, this.roleOptionAdminPrivilege];
    public loading: boolean = false;

    private _user: User;

    constructor(private formBuilder: FormBuilder) {
        this.form = this.formBuilder.group(
            {
                username: this.formBuilder.control('', [Validators.required]),
                displayName: this.formBuilder.control('', [Validators.required]),
                email: this.formBuilder.control('', [Validators.required, Validators.email]),
                adminRole: this.formBuilder.control(false, []),
                // icon: this.formBuilder.control('', [Validators.required]), cloud provider
            }
        );
    }

    ngOnInit() {
    }

    @Input()
    public get user(): User {
        return this._user;
    }

    public set user(user: User) {
        this._user = user;

        this.resetForm();
    }

    public getUrl() {
        return (this._user != null)
            ? `url('/api/user/${this.user.id}/avatar')`
            : null;
    }

    public resetForm() {
        this.form.controls['username'].setValue(this.user.username);
        if (this.user.isExternal()) {
            this.form.controls['username'].disable();
        }

        this.form.controls['displayName'].setValue(this.user.displayName);
        if (this.user.isExternal()) {
            this.form.controls['displayName'].disable();
        }

        this.form.controls['email'].setValue(this.user.email);
        if (this.user.isExternal()) {
            this.form.controls['email'].disable();
        }

        this.form.controls['adminRole'].setValue(
            _.some(this.user.roles, role => role === UserRole.ADMIN) ? this.roleOptionAdminPrivilege : this.roleOptionNoPrivilege
        );

        if (this.user.isAdminUser()) {
            this.form.disable()
        }

        this.form.markAsPristine();
    }


    public onSave() {
        this.loading = true;

        // if (this.locale.id) {
        //   this.translationLocaleService
        //       .updateLocale(this.toUpdatedLocale())
        //       .toPromise()
        //       .then(translationLocale => this.locale = translationLocale)
        //       .then(translationLocale => this.save.emit(translationLocale))
        //       .catch(error => {
        //         this.notificationService.displayErrorMessage('ADMIN.LOCALES.ERROR.UPDATE', error);
        //       })
        //       .finally(() => this.loading = false);
        // } else {
        //   this.translationLocaleService
        //       .createLocale(this.toNewLocale())
        //       .toPromise()
        //       .then(translationLocale => this.locale = translationLocale)
        //       .then(translationLocale => this.save.emit(translationLocale))
        //       .catch(error => {
        //         this.notificationService.displayErrorMessage('ADMIN.LOCALES.ERROR.SAVE', error);
        //       })
        //       .finally(() => this.loading = false);
        // }
    }

    public onDelete() {
        // if (this.locale.id) {
        //   this.loading = true;
        //   this.translationLocaleService
        //       .deleteLocale(this.locale.id)
        //       .toPromise()
        //       .then(translationLocale => this.locale = translationLocale)
        //       .then(translationLocale => this.save.emit(translationLocale))
        //       .catch(error => {
        //         this.notificationService.displayErrorMessage('ADMIN.LOCALES.ERROR.DELETE', error);
        //       })
        //       .finally(() => this.loading = false);
        // } else {
        //   this.delete.emit();
        // }
    }
}
