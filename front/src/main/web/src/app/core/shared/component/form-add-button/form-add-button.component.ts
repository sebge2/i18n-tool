import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormGroup} from "@angular/forms";

@Component({
    selector: 'app-form-add-button',
    templateUrl: './form-add-button.component.html',
    styleUrls: ['./form-add-button.component.css']
})
export class FormAddButtonComponent {

    @Input() public form: FormGroup;
    @Input() public disabled: boolean;
    @Input() public addInProgress: boolean;
    @Input() public addIcon = 'library_add';
    @Output() public add = new EventEmitter<void>();

    constructor() {
    }

    public onAdd() {
        this.add.emit();
    }

}
