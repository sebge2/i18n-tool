import {Component, Input, TemplateRef} from '@angular/core';

@Component({
    selector: 'app-table-cell',
    template: '',
})
export class TableCellComponent {

    @Input() public columnId: string;
    @Input() public template: TemplateRef<any>;

    constructor() {
    }
}
