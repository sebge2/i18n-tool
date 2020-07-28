import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthenticationService} from "../../../../core/auth/service/authentication.service";
import {User} from "../../../../core/auth/model/user.model";
import {Subject} from "rxjs";

@Component({
    selector: 'app-edit-profile',
    templateUrl: './edit-profile.component.html',
    styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit, OnDestroy {

    public readonly form: FormGroup;
    public loading = false;
    public currentUser: User;

    private readonly _destroyed$ = new Subject();

    constructor(private formBuilder: FormBuilder,
                private authenticationService: AuthenticationService) {
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

    }

    public resetForm() {
        this.form.controls['username'].setValue(this.currentUser.username);
        this.form.controls['displayName'].setValue(this.currentUser.displayName);
        this.form.controls['email'].setValue(this.currentUser.email);
        this.form.controls['avatar'].setValue(null);

        this.form.markAsPristine();
        this.form.markAsUntouched();
    }
}
