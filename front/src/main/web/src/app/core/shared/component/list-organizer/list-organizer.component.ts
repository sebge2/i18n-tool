import {Component, EventEmitter, Input, Output, TemplateRef} from '@angular/core';
import {CdkDragDrop} from '@angular/cdk/drag-drop';

const AVAILABLE_LIST: string = 'availableList';
const SELECTED_LIST: string = 'selectedList';

@Component({
    selector: 'app-list-organizer',
    templateUrl: './list-organizer.component.html',
    styleUrls: ['./list-organizer.component.scss']
})
export class ListOrganizerComponent<A, S> {

    @Input() public available: string;
    @Input() public selected: string;
    @Input() public availableElements: A[] = [];
    @Input() public selectedElements: S[] = [];
    @Input() public availableElementTemplate: TemplateRef<any>;
    @Input() public selectedElementTemplate: TemplateRef<any>;

    @Output() public select = new EventEmitter<{ fromIndex: number, toIndex: number }>();
    @Output() public unselect = new EventEmitter<{ fromIndex: number, toIndex: number }>();
    @Output() public selectAll = new EventEmitter<any>();
    @Output() public unselectAll = new EventEmitter<any>();
    @Output() public moveAvailable = new EventEmitter<{ fromIndex: number, toIndex: number }>();
    @Output() public moveSelected = new EventEmitter<{ fromIndex: number, toIndex: number }>();

    public onDrop(event: CdkDragDrop<string[]>): void {
        if (event.previousContainer === event.container) {
            if (event.container.id === AVAILABLE_LIST) {
                this.moveAvailable.emit({fromIndex: event.previousIndex, toIndex: event.currentIndex});
            } else if (event.container.id == SELECTED_LIST) {
                this.moveSelected.emit({fromIndex: event.previousIndex, toIndex: event.currentIndex});
            }
        } else {
            if (event.container.id === AVAILABLE_LIST) {
                this.unselect.emit({fromIndex: event.previousIndex, toIndex: event.currentIndex});
            } else if (event.container.id == SELECTED_LIST) {
                this.select.emit({fromIndex: event.previousIndex, toIndex: event.currentIndex});
            }
        }
    }

    public onSelectAll() {
        this.selectAll.emit();
    }

    public onUnselectAll() {
        this.unselectAll.emit();
    }
}
