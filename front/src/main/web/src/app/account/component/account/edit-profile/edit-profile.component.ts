import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthenticationService} from "../../../../core/auth/service/authentication.service";
import {User} from "../../../../core/auth/model/user.model";
import {Subject} from "rxjs";
import {UserService} from "../../../../core/auth/service/user.service";
import {NotificationService} from "../../../../core/notification/service/notification.service";
import {ImportedFile} from "../../../../core/shared/model/imported-file.model";

@Component({
    selector: 'app-edit-profile',
    templateUrl: './edit-profile.component.html',
    styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit, OnDestroy {

    public readonly form: FormGroup;
    public currentUser: User;

    public cancelInProgress: boolean = false;
    public saveInProgress: boolean = false;

    private readonly _destroyed$ = new Subject();

    constructor(private formBuilder: FormBuilder,
                private authenticationService: AuthenticationService,
                private notificationService: NotificationService,
                private userService: UserService) {
        this.form = this.formBuilder.group(
            {
                username: this.formBuilder.control('', [Validators.required]),
                displayName: this.formBuilder.control('', [Validators.required]),
                email: this.formBuilder.control('', [Validators.required, Validators.email]),
                avatar: this.formBuilder.control(null, []),
            }
        );
    }

    public ngOnInit() {
        this.authenticationService.currentUser()
            .subscribe(currentUser => {
                this.currentUser = currentUser;
                this.resetForm();
            });
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public onSave() {
        this.saveInProgress = true;

        this.saveAvatar()
            .then(_ => this.saveProfile())
            .then(_ => this.makeFormUntouched())
            .catch(error => this.notificationService.displayErrorMessage('ACCOUNT.ERROR.SAVE_PROFILE', error))
            .finally(() => this.saveInProgress = false);
    }

    public resetForm() {
        this.cancelInProgress = true;

        this.form.controls['username'].setValue(this.currentUser.username);
        this.form.controls['displayName'].setValue(this.currentUser.displayName);
        this.form.controls['email'].setValue(this.currentUser.email);
        this.form.controls['avatar'].setValue(null);

        if (this.isEditionNotAllowed()) {
            this.form.disable();
        }

        this.makeFormUntouched();
        this.cancelInProgress = false;
    }

    public get actionInProgress(): boolean {
        return this.cancelInProgress || this.saveInProgress;
    }

    private makeFormUntouched() {
        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

    private isEditionNotAllowed() {
        return this.currentUser.isAdminUser() || this.currentUser.isExternal();
    }

    private saveAvatar() : Promise<any> {
        if (!this.form.controls['avatar'].pristine) {
            return this.userService
                .updateCurrentUserAvatar(<ImportedFile>this.form.controls['avatar'].value)
                .toPromise();
        } else {
            return Promise.resolve();
        }
    }

    private saveProfile(): Promise<any> {
        return this.userService
            .updateCurrentUser({
                displayName: this.form.controls['displayName'].value,
                username: this.form.controls['username'].value,
                email: this.form.controls['email'].value
            })
            .toPromise();
    }
}
