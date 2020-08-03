import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RepositoryType} from "../../../../../../translations/model/repository-type.model";
import {GitHubRepositoryCreationRequestDto, GitRepositoryCreationRequestDto} from "../../../../../../api";

@Component({
    selector: 'app-repository-add-wizard-step-info',
    templateUrl: './repository-add-wizard-step-info.component.html',
    styleUrls: ['./repository-add-wizard-step-info.component.css']
})
export class RepositoryAddWizardStepInfoComponent {

    @Input() public form: FormGroup;

    public RepositoryType = RepositoryType;

    private _repositoryType: RepositoryType;

    constructor(private formBuilder: FormBuilder) {
    }

    @Input()
    public get repositoryType(): RepositoryType {
        return this._repositoryType;
    }

    public set repositoryType(repositoryType: RepositoryType) {
        this._repositoryType = repositoryType;

        if (this.repositoryType == RepositoryType.GIT) {
            this.form.setControl('location', this.formBuilder.control('', [Validators.required]));
            this.form.setControl('name', this.formBuilder.control('', [Validators.required]));
        } else if (this.repositoryType == RepositoryType.GITHUB) {
            this.form.setControl('username', this.formBuilder.control('', [Validators.required]));
            this.form.setControl('repository', this.formBuilder.control('', [Validators.required]));
            this.form.setControl('accessKey', this.formBuilder.control('', []));
        }
    }

    public get creationRequest(): (GitHubRepositoryCreationRequestDto | GitRepositoryCreationRequestDto) {
        if (this.repositoryType == RepositoryType.GIT) {
            return <GitRepositoryCreationRequestDto>{
                type: "GIT",
                location: this.form.controls['location'].value,
                name: this.form.controls['name'].value
            };
        } else if (this.repositoryType == RepositoryType.GITHUB) {
            return <GitHubRepositoryCreationRequestDto>{
                type: "GITHUB",
                username: this.form.controls['username'].value,
                repository: this.form.controls['repository'].value,
                accessKey: this.form.controls['accessKey'].value,
            };
        } else {
            throw Error(`Unsupported repository type ${this.repositoryType}.`);
        }
    }
}
