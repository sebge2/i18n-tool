import {Component, OnInit} from '@angular/core';
import {MatProgressButtonOptions} from 'mat-progress-buttons';
import {AuthenticationService} from "../../../service/authentication.service";
import {AuthenticationErrorType} from "../../../model/authentication-error-type.model";
import {Router} from "@angular/router";
import {NotificationService} from "../../../../notification/service/notification.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthenticatedUser} from '../../../model/authenticated-user.model';

@Component({
    selector: 'app-login-auth-key',
    templateUrl: './login-auth-key.component.html',
    styleUrls: ['./login-auth-key.component.css']
})
export class LoginAuthKeyComponent implements OnInit {

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
            fontIcon: 'send'
        }
    };

    form: FormGroup;

    constructor(private authenticationService: AuthenticationService,
                private notificationService: NotificationService,
                private formBuilder: FormBuilder,
                private router: Router) {
    }

    ngOnInit() {
        this.form = this.formBuilder.group({
            authkey: ['', Validators.required]
        });
    }

    public login(): void {
        this.buttonOptions.active = true;

        if (this.form.errors && this.form.errors['authFailed']) {
            delete this.form.errors['authFailed'];
        }

        this.authenticationService.authenticateWithGitHubAuthKey(this.form.get('authkey').value)
            .toPromise()
            .then(
                (user: AuthenticatedUser) => {
                    this.router.navigate(['/translations']);
                }
            )
            .catch(
                (errorType: AuthenticationErrorType) => {
                    if (errorType == AuthenticationErrorType.WRONG_CREDENTIALS) {
                        this.form.setErrors({'authFailed': true});
                    }
                }
            )
            .finally(
                () => {
                    this.buttonOptions.active = false;
                }
            );
    }

    paste() {
        navigator.clipboard.readText()
            .then(text => {
                this.form.get('authkey').setValue(text);
            })
            .catch(err => {
                console.error('Failed to read clipboard contents.', err);

                this.notificationService.displayErrorMessage('Failed to read clipboard contents.');
            });
    }
}
