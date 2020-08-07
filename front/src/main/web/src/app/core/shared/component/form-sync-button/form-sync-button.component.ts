import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'app-form-sync-button',
    templateUrl: './form-sync-button.component.html',
    styleUrls: ['./form-sync-button.component.css']
})
export class FormSyncButtonComponent {

    @Input() public syncInProgress: boolean;
    @Input() public disabled: boolean;
    @Output() public sync = new EventEmitter<void>();

    constructor() {
    }

    public onSync() {
        this.sync.emit();
    }

}
