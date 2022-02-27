import { Component, TemplateRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-table-top-header-row',
  templateUrl: './table-top-header-row.component.html',
})
export class TableTopHeaderRowComponent {
  @ViewChild(TemplateRef, { read: TemplateRef }) template: TemplateRef<any>;

  constructor() {}
}
