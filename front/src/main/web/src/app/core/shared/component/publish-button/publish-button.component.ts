import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'app-publish-button',
    templateUrl: './publish-button.component.html'
})
export class PublishButtonComponent {

    @Input() public disabled: boolean = false;
    @Output() public publish = new EventEmitter<void>();

    constructor() {
    }

    public onClick() {
        this.publish.emit(null);
    }
}
