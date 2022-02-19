import {Component, EventEmitter, Output} from '@angular/core';

@Component({
    selector: 'app-main-add-button',
    templateUrl: './main-add-button.component.html',
    styleUrls: ['./main-add-button.component.scss']
})
export class MainAddButtonComponent {

    @Output() add = new EventEmitter<void>();

    constructor() {
    }

    onAdd() {
        this.add.emit();
    }
}
