import { Component, ContentChildren, QueryList } from '@angular/core';
import { TableCellComponent } from '../table-cell/table-cell.component';

@Component({
  selector: 'app-table-row',
  template: '<ng-template><ng-content></ng-content></ng-template>',
})
export class TableRowComponent {
  @ContentChildren(TableCellComponent) public cellComponents: QueryList<TableCellComponent>;

  constructor() {}
}
