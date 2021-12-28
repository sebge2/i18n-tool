import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'app-generic-button',
    templateUrl: './generic-button.component.html',
})
export class GenericButtonComponent {

    @Input() icon: string;
    @Input() tooltip: string;
    @Output() click = new EventEmitter<void>();

    constructor() {
    }

    onClick() {
        this.click.emit();
    }
}
