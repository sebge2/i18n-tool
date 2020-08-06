import {Component, EventEmitter, Output} from '@angular/core';

@Component({
    selector: 'app-form-download-action-button',
    templateUrl: './form-download-action-button.component.html',
    styleUrls: ['./form-download-action-button.component.css']
})
export class FormDownloadActionButtonComponent {

    @Output() public start = new EventEmitter<void>();

    constructor() {
    }

    public onStart() {
        this.start.emit();
    }
}
