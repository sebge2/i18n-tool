import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Repository} from "../../../../../translations/model/repository/repository.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RepositoryType} from "../../../../../translations/model/repository/repository-type.model";
import {RepositoryStatus} from "../../../../../translations/model/repository/repository-status.model";
import {AuthenticatedUser} from "../../../../../core/auth/model/authenticated-user.model";
import {AuthenticationService} from "../../../../../core/auth/service/authentication.service";
import {Observable} from "rxjs";

@Component({
    selector: 'app-repository-view-card',
    templateUrl: './repository-view-card.component.html',
    styleUrls: ['./repository-view-card.component.css']
})
export class RepositoryViewCardComponent {

    @Input() public repository: Repository;
    @Output() public save = new EventEmitter<Repository>();
    @Output() public open = new EventEmitter<Repository>();

    public readonly form: FormGroup;
    public readonly repositoryStatus = RepositoryStatus;
    public readonly types = [RepositoryType.GIT, RepositoryType.GITHUB];

    public readonly currentAuthenticatedUser: Observable<AuthenticatedUser> = this.authenticationService.currentAuthenticatedUser();

    constructor(private formBuilder: FormBuilder,
                private authenticationService: AuthenticationService) {
        this.form = this.formBuilder.group(
            {
                type: this.formBuilder.control('', [Validators.required])
            }
        );
    }

    public onOpen() {
        this.open.emit(this.repository);
    }
}
