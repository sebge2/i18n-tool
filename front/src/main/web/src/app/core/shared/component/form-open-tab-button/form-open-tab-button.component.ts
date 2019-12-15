import {Component, EventEmitter, Output} from '@angular/core';

@Component({
    selector: 'app-form-open-tab-button',
    templateUrl: './form-open-tab-button.component.html',
})
export class FormOpenTabButtonComponent {

    @Output() public open = new EventEmitter<void>();

    constructor() {
    }

    public onOpen() {
        this.open.emit();
    }
}
