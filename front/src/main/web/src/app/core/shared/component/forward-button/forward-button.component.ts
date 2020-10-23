import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'app-forward-button',
    templateUrl: './forward-button.component.html',
})
export class ForwardButtonComponent {

    @Input() public disabled: boolean = false;
    @Output() public publish = new EventEmitter<void>();

    constructor() {
    }

    public onClick() {
        this.publish.emit(null);
    }
}

