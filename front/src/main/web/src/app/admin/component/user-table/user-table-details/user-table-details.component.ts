import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MatProgressButtonOptions} from "mat-progress-buttons";
import {UserService} from "../../../../core/auth/service/user.service";
import {User} from "../../../../core/auth/model/user.model";
import {UserUpdate} from "../../../../core/auth/model/user-update.model";
import {UserRole} from "../../../../core/auth/model/user-role.model";

@Component({
    selector: 'app-user-table-details',
    templateUrl: './user-table-details.component.html',
    styleUrls: ['./user-table-details.component.css']
})
export class UserTableDetailsComponent implements OnInit {

    buttonOptions: MatProgressButtonOptions = {
        active: false,
        text: 'Save',
        spinnerSize: 18,
        raised: true,
        stroked: false,
        buttonColor: 'primary',
        spinnerColor: 'accent',
        fullWidth: false,
        disabled: false,
        mode: 'indeterminate',
        buttonIcon: {
            fontIcon: 'save'
        }
    };

    form: FormGroup;
    hidePassword = true;

    constructor(private formBuilder: FormBuilder,
                private userService: UserService) {
        this.form = this.formBuilder.group({
            id: [],
            adminRole: [],
            password: [null, Validators.pattern("^$|^.{5,}$")]
        });
    }

    ngOnInit() {
    }

    @Input()
    set user(user: User) {
        this.form.controls['id'].setValue(user.id);
        this.form.controls['adminRole'].setValue(user.hasAdminRole());
    }

    save() {
        const userUpdate = new UserUpdate();

        if (this.form.controls['adminRole'].value) {
            userUpdate.roles = [UserRole.ADMIN];
        } else {
            userUpdate.roles = [];
        }

        if ((this.form.controls['password'].value) && (this.form.controls['password'].value.toString().length > 0)) {
            userUpdate.password = this.form.controls['password'].value;
        }

        this.userService
            .updateUser(this.form.controls['id'].value, userUpdate)
            .toPromise()
            .then(user => {
                this.form.controls['password'].setValue(null);
            });
    }
}
