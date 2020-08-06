import {Component, EventEmitter, Output} from '@angular/core';

@Component({
    selector: 'app-form-sync-button',
    templateUrl: './form-sync-button.component.html',
    styleUrls: ['./form-sync-button.component.css']
})
export class FormSyncButtonComponent {

    @Output() public sync = new EventEmitter<void>();

    constructor() {
    }

    public onSync() {
        this.sync.emit();
    }

}
