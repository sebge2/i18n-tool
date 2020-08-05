import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Repository} from "../../../../../translations/model/repository.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RepositoryType} from "../../../../../translations/model/repository-type.model";

@Component({
    selector: 'app-repository-view-card',
    templateUrl: './repository-view-card.component.html',
    styleUrls: ['./repository-view-card.component.css']
})
export class RepositoryViewCardComponent implements OnInit {

    @Input() public repository: Repository;
    @Output() public save = new EventEmitter<Repository>();
    @Output() public open = new EventEmitter<Repository>();

    public readonly form: FormGroup;
    public readonly types = [RepositoryType.GIT, RepositoryType.GITHUB];

    constructor(private formBuilder: FormBuilder) {
        this.form = this.formBuilder.group(
            {
                type: this.formBuilder.control('', [Validators.required])
            }
        );
    }

    public ngOnInit() {
    }

    public onOpen() {
        this.open.emit(this.repository);
    }
}
