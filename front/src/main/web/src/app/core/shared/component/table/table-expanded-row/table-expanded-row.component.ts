import { Component, Input, TemplateRef } from '@angular/core';

@Component({
  selector: 'app-table-expanded-row',
  template: '<ng-template><ng-content></ng-content></ng-template>',
})
export class TableExpandedRowComponent {
  @Input() public template: TemplateRef<any>;

  constructor() {}
}
