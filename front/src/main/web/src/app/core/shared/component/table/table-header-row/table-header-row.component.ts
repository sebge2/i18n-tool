import { AfterContentInit, Component, ContentChildren, Input, OnDestroy, QueryList } from '@angular/core';
import { TableHeaderComponent } from '../table-header/table-header.component';
import * as _ from 'lodash';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { DomSanitizer, SafeStyle } from '@angular/platform-browser';

@Component({
  selector: 'app-table-header-row',
  template: '<ng-content></ng-content>',
})
export class TableHeaderRowComponent implements AfterContentInit, OnDestroy {
  @Input() public sticky: boolean = false;
  @ContentChildren(TableHeaderComponent) public headerComponents: QueryList<TableHeaderComponent>;

  public displayedColumns: string[];
  public headers: Map<string, TableHeaderComponent>;
  public tableStyle: SafeStyle;
  public expandedRowStyle: SafeStyle;

  private readonly _destroyed$ = new Subject<void>();

  constructor(private _sanitizer: DomSanitizer) {}

  public ngAfterContentInit(): void {
    this.headerComponents.changes.pipe(takeUntil(this._destroyed$)).subscribe(() => this.updateHeaders());

    setTimeout(() => this.updateHeaders());
  }

  public ngOnDestroy(): void {
    this._destroyed$.next();
    this._destroyed$.complete();
  }

  private updateHeaders() {
    this.headers = new Map<string, TableHeaderComponent>();

    for (const headerComponent of this.headerComponents.toArray()) {
      this.headers.set(headerComponent.columnId, headerComponent);
    }

    this.displayedColumns = _.map(this.headerComponents.toArray(), (header) => header.columnId);
    this.tableStyle = this._sanitizer.bypassSecurityTrustStyle(
      _.chain(this.headerComponents.toArray())
        .map((header) => `${header.columnGridDef}`)
        .join(' ')
        .value()
    );
    this.expandedRowStyle = this._sanitizer.bypassSecurityTrustStyle(`1 / ${this.headerComponents.length + 1}`);
  }
}
