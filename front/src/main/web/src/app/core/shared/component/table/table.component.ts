import {
    AfterContentInit,
    Component,
    ContentChild,
    ContentChildren,
    Inject,
    Input,
    OnDestroy,
    QueryList,
    TemplateRef
} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {TableHeaderComponent} from "./table-header/table-header.component";
import {takeUntil} from "rxjs/operators";
import {Subject} from "rxjs";
import * as _ from "lodash";
import {TableCellComponent} from "./table-cell/table-cell.component";
import {TableExpandedRowComponent} from "./table-expanded-row/table-expanded-row.component";
import {DOCUMENT} from "@angular/common";
import {DomSanitizer, SafeStyle} from "@angular/platform-browser";

@Component({
    selector: 'app-table',
    templateUrl: './table.component.html',
    styleUrls: ['./table.component.scss'],
})
export class TableComponent<E> implements AfterContentInit, OnDestroy {

    @Input() public readonly dataSource = new MatTableDataSource<E>();

    @ContentChildren(TableHeaderComponent) public headerComponents: QueryList<TableHeaderComponent>;
    @ContentChildren(TableCellComponent) public cellComponents: QueryList<TableCellComponent>;
    @ContentChild(TableExpandedRowComponent) public expandedRowComponent: TableExpandedRowComponent;

    public displayedColumns: string[];
    public headers: Map<string, TableHeaderComponent>;
    public cells: Map<string, TableCellComponent>;

    public tableStyle: SafeStyle;
    public expandedRowStyle: SafeStyle;
    public expandedElement: E | undefined;
    public hoveredElement: E | undefined;

    private readonly _destroyed$ = new Subject<void>();

    constructor(@Inject(DOCUMENT) private _document: Document,
                private _sanitizer: DomSanitizer) {
    }

    public ngAfterContentInit(): void {
        this.headerComponents.changes
            .pipe(takeUntil(this._destroyed$))
            .subscribe(() => this.updateHeaders());

        this.cellComponents.changes
            .pipe(takeUntil(this._destroyed$))
            .subscribe(() => this.updateCells());

        setTimeout(() => this.updateHeaders());
        setTimeout(() => this.updateCells());
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public get hasExpandedRow(): boolean {
        return !!this.expandedRowComponent;
    }

    public get expandedDivStyle(): any {
        return {'grid-column': '1 / 6'}
    }

    public getHeaderTemplate(column: string): TemplateRef<any> {
        return this.headers.get(column).template;
    }

    public getCellTemplate(column: string): TemplateRef<any> {
        return this.cells.get(column).template;
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

        this.expandedElement = (this.expandedElement === expandedElement) ? null : expandedElement;
    }

    private updateHeaders() {
        this.headers = new Map<string, TableHeaderComponent>();

        for (const headerComponent of this.headerComponents.toArray()) {
            this.headers.set(headerComponent.columnId, headerComponent);
        }

        this.displayedColumns = _.map(this.headerComponents.toArray(), header => header.columnId);
        this.tableStyle = this._sanitizer.bypassSecurityTrustStyle(_.chain(this.headerComponents.toArray()).map(header => `${header.columnGridDef}`).join(' ').value());
        this.expandedRowStyle = this._sanitizer.bypassSecurityTrustStyle(`1 / ${(this.headerComponents.length + 1)}`);
    }

    private updateCells() {
        this.cells = new Map<string, TableCellComponent>();

        for (const cellComponent of this.cellComponents.toArray()) {
            this.cells.set(cellComponent.columnId, cellComponent);
        }
    }
}
