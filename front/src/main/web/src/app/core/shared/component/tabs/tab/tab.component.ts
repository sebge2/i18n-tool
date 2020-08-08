import {Component, EventEmitter, Input, Output, TemplateRef, ViewChild} from '@angular/core';
import {MouseEventUtils} from "../../../utils/mouse-event-utils";
import {generateId} from "../../../utils/string-utils";

@Component({
    selector: 'app-tab',
    templateUrl: './tab.component.html',
    styleUrls: ['./tab.component.css']
})
export class TabComponent {

    @Input() public id: string = generateId();
    @Input() public title: string;
    @Input() public matIcon: string;
    @Input() public iconClass: string;
    @Input() public closeable: boolean = false;

    @Output() public close = new EventEmitter<void>();

    @ViewChild(TemplateRef, {static: false}) template: TemplateRef<any>;

    constructor() {
    }

    public onClose($event) {
        MouseEventUtils.stopPropagation($event);
        this.close.emit();
    }
}
