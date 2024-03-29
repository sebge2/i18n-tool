import { Component, Input, TemplateRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-table-header',
  templateUrl: './table-header.component.html',
})
export class TableHeaderComponent {
  @Input() public columnId: string;
  @Input() public columnGridDef: string = '1fr';
  @ViewChild(TemplateRef, { read: TemplateRef }) template: TemplateRef<any>;

  constructor() {}
}
