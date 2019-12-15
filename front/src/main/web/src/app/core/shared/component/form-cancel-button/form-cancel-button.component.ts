import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormGroup} from "@angular/forms";

@Component({
    selector: 'app-form-cancel-button',
    templateUrl: './form-cancel-button.component.html',
})
export class FormCancelButtonComponent implements OnInit {

    @Input() public form: FormGroup;
    @Input() public disabled: boolean;
    @Input() public cancelInProgress: boolean;
    @Output() public reset = new EventEmitter<void>();

    constructor() {
    }

    ngOnInit() {
    }

    public onReset() {
        this.reset.emit();
    }
}
