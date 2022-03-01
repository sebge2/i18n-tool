import {Component, OnInit} from '@angular/core';
import {MatProgressButtonOptions} from 'mat-progress-buttons';
import {AuthenticationService} from '../../../service/authentication.service';
import {AuthenticationErrorType} from '../../../model/authentication-error-type.model';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {NotificationService} from '@i18n-core-notification';
import {mergeMap} from "rxjs";
import {catchError} from "rxjs/operators";
import {RedirectService} from "../../../service/redirect.service";

@Component({
    selector: 'app-login-user-password',
    templateUrl: './login-user-password.component.html',
    styleUrls: ['./login-user-password.component.css'],
})
export class LoginUserPasswordComponent implements OnInit {
    buttonOptions: MatProgressButtonOptions = {
        active: false,
        text: 'Login',
        spinnerSize: 18,
        raised: true,
        stroked: false,
        buttonColor: 'primary',
        spinnerColor: 'accent',
        fullWidth: false,
        disabled: false,
        mode: 'indeterminate',
        buttonIcon: {
            fontIcon: 'send',
        },
    };

    form: FormGroup;

    constructor(
        private authenticationService: AuthenticationService,
        private _redirectService: RedirectService,
        private notificationService: NotificationService,
        private formBuilder: FormBuilder,
    ) {
    }

    ngOnInit() {
        this.form = this.formBuilder.group({
            username: ['', Validators.required],
            password: ['', Validators.required],
        });
    }

    login(): void {
        this.buttonOptions.active = true;

        if (this.form.errors && this.form.errors['authFailed']) {
            delete this.form.errors['authFailed'];
        }

        this.authenticationService
            .authenticateWithUserPassword(this.form.get('username').value, this.form.get('password').value)
            .pipe(
                mergeMap(() => this._redirectService.redirectAfterLogin()),
                catchError((error: Error) => {
                    this.buttonOptions.active = false;

                    if (error.message == AuthenticationErrorType.WRONG_CREDENTIALS) {
                        this.form.setErrors({authFailed: true});
                        return null;
                    } else if (error.message == AuthenticationErrorType.AUTHENTICATION_SYSTEM_ERROR) {
                        console.error('Error while authenticating user.', error);
                        this.notificationService.displayErrorMessage('Error while authenticating user.');
                    }
                })
            )
            .subscribe(() => this.buttonOptions.active = false);
    }
}
