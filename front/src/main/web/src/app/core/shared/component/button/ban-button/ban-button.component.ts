import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'app-ban-button',
    templateUrl: './ban-button.component.html',
})
export class BanButtonComponent {

    @Input() public banInProgress: boolean;
    @Input() public disabled: boolean = false;
    @Output() public publish = new EventEmitter<void>();

    constructor() {
    }

    public onClick() {
        this.publish.emit(null);
    }

}
