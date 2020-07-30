import {Component, OnInit} from '@angular/core';
import {MatProgressButtonOptions} from 'mat-progress-buttons';
import {AuthenticationService} from "../../../service/authentication.service";
import {AuthenticationErrorType} from "../../../model/authentication-error-type.model";
import {Router} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
    selector: 'app-login-user-password',
    templateUrl: './login-user-password.component.html',
    styleUrls: ['./login-user-password.component.css']
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
            fontIcon: 'send'
        }
    };

    form: FormGroup;

    constructor(private authenticationService: AuthenticationService,
                private formBuilder: FormBuilder,
                private router: Router) {
    }

    ngOnInit() {
        this.form = this.formBuilder.group({
            username: ['', Validators.required],
            password: ['', Validators.required]
        });
    }

    public login(): void {
        this.buttonOptions.active = true;

        if (this.form.errors && this.form.errors['authFailed']) {
            delete this.form.errors['authFailed'];
        }

        this.authenticationService.authenticateWithUserPassword(this.form.get('username').value, this.form.get('password').value)
            .then((_) => this.router.navigate(['/translations']))
            .catch((error: Error) => {
                if (error.message == AuthenticationErrorType.WRONG_CREDENTIALS) {
                    this.form.setErrors({'authFailed': true});
                }
                // TODO technical error
            })
            .finally(() => this.buttonOptions.active = false);
    }
}
