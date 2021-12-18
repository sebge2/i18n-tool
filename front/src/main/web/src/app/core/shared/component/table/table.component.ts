import { AfterContentInit, Component, ContentChild, Inject, Input, OnDestroy, TemplateRef } from '@angular/core';
import { TableHeaderComponent } from './table-header/table-header.component';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import * as _ from 'lodash';
import { TableCellComponent } from './table-cell/table-cell.component';
import { DOCUMENT } from '@angular/common';
import { DomSanitizer, SafeStyle } from '@angular/platform-browser';
import { TableTopHeaderRowComponent } from './table-top-header-row/table-top-header-row.component';
import { FormArray, FormGroup } from '@angular/forms';
import { TableRowComponent } from './table-row/table-row.component';
import { TableExpandedRowComponent } from './table-expanded-row/table-expanded-row.component';
import { TableHeaderRowComponent } from './table-header-row/table-header-row.component';

export interface TableDataSource<E> {
  data: E[];
}

export class SimpleTableDataSource<E> implements TableDataSource<E> {
  constructor(public data: E[]) {}
}

export class FormTableDataSource implements TableDataSource<FormGroup> {
  constructor(public form: FormArray) {}

  get data(): FormGroup[] {
    return <FormGroup[]>this.form.controls;
  }
}

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss'],
})
export class TableComponent<E> implements AfterContentInit, OnDestroy {
  @Input() public readonly dataSource: TableDataSource<E> = new SimpleTableDataSource<E>([]);
  @Input() public autoscroll: boolean = false;

  @ContentChild(TableTopHeaderRowComponent) public topHeaderComponent: TableTopHeaderRowComponent;
  @ContentChild(TableHeaderRowComponent) public headerComponent: TableHeaderRowComponent;
  @ContentChild(TableRowComponent) public rowComponent: TableRowComponent;
  @ContentChild(TableExpandedRowComponent) public expandedRowComponent: TableExpandedRowComponent;

  public cells: Map<string, TableCellComponent>;
  public expandedElement: E | undefined;
  public hoveredElement: E | undefined;

  private readonly _destroyed$ = new Subject<void>();

  constructor(@Inject(DOCUMENT) private _document: Document, private _sanitizer: DomSanitizer) {}

  public ngAfterContentInit(): void {
    this.rowComponent.cellComponents.changes.pipe(takeUntil(this._destroyed$)).subscribe(() => this.updateCells());

    setTimeout(() => this.updateCells());
  }

  public ngOnDestroy(): void {
    this._destroyed$.next();
    this._destroyed$.complete();
  }

  public get hasExpandedRow(): boolean {
    return !!this.expandedRowComponent;
  }

  public getHeaderComponent(column: string): TableHeaderComponent {
    return this.headerComponent.headers.get(column);
  }

  public getCellComponent(column: string): TableCellComponent {
    return this.cells.get(column);
  }

  public getExpandedRowTemplate(): TemplateRef<any> | undefined {
    return _.get(this.expandedRowComponent, 'template');
  }

  public onMouseOver(element: E) {
    this.hoveredElement = element;
  }

  public onMouseLeave(element: E) {
    if (this.hoveredElement === element) {
      this.hoveredElement = null;
    }
  }

  public onToggleExpand(expandedElement: E) {
    if (!this.hasExpandedRow) {
      return;
    }

    this.expandedElement = this.expandedElement === expandedElement ? null : expandedElement;
  }

  private updateCells() {
    this.cells = new Map<string, TableCellComponent>();

    for (const cellComponent of this.rowComponent.cellComponents.toArray()) {
      this.cells.set(cellComponent.columnId, cellComponent);
    }
  }
}
