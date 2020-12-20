import {AfterContentInit, Component, ContentChildren, Input, OnDestroy, QueryList} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {TableHeaderComponent} from "./table-header/table-header.component";
import {takeUntil} from "rxjs/operators";
import {Subject} from "rxjs";
import * as _ from "lodash";
import {TableCellComponent} from "./table-cell/table-cell.component";

@Component({
    selector: 'app-table',
    templateUrl: './table.component.html',
    styleUrls: ['./table.component.scss']
})
export class TableComponent<E> implements AfterContentInit, OnDestroy {

    @Input() public readonly dataSource = new MatTableDataSource<E>();

    @ContentChildren(TableHeaderComponent) public headerComponents: QueryList<TableHeaderComponent>;
    @ContentChildren(TableCellComponent) public cellComponents: QueryList<TableCellComponent>;

    public displayedColumns: string[];
    public headers: Map<string, TableHeaderComponent>;
    public cells: Map<string, TableCellComponent>;

    private _destroyed$ = new Subject<void>();

    constructor() {
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

    public getHeaderTemplate(column: string) {
        return this.headers.get(column).template;
    }

    public getCellTemplate(column: string) {
        return this.cells.get(column).template;
    }

    private updateHeaders() {
        this.headers = new Map<string, TableCellComponent>();

        for (const headerComponent of this.headerComponents.toArray()) {
            this.headers.set(headerComponent.columnId, headerComponent);
        }

        this.displayedColumns = _.map(this.headerComponents.toArray(), header => header.columnId);
    }

    private updateCells() {
        this.cells = new Map<string, TableCellComponent>();

        for (const cellComponent of this.cellComponents.toArray()) {
            this.cells.set(cellComponent.columnId, cellComponent);
        }
    }
}
