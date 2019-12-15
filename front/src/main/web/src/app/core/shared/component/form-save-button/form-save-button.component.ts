import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormGroup} from "@angular/forms";

@Component({
    selector: 'app-form-save-button',
    templateUrl: './form-save-button.component.html',
})
export class FormSaveButtonComponent implements OnInit {

    @Input() public form: FormGroup;
    @Input() public disabled: boolean;
    @Input() public saveInProgress: boolean;
    @Output() public save = new EventEmitter<void>();

    constructor() {
    }

    ngOnInit() {
    }

    public onSave() {
        this.save.emit();
    }

}
