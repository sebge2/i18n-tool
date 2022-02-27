import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MouseEventUtils} from "../../../utils/mouse-event-utils";

@Component({
    selector: 'app-generic-button',
    templateUrl: './generic-button.component.html',
    styleUrls: ['./generic-button.component.scss']
})
export class GenericButtonComponent {

    @Input() color:  'primary' | 'accent' | 'warn' | '' = '';
    @Input() icon: string;
    @Input() label: string;
    @Input() tooltip: string;
    @Output() click = new EventEmitter<void>();

    constructor() {
    }

    onClick(event: MouseEvent) {
        this.click.emit();

        MouseEventUtils.stopPropagation(event);
    }
}
