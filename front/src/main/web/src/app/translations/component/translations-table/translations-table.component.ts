import {Component, Input, OnInit} from '@angular/core';
import {TranslationsSearchRequest} from "../../model/translations-search-request.model";
import {TranslationsService} from '../../service/translations.service';
import {BundleFile} from "../../model/edition/bundle-file.model";
import {BundleKey} from '../../model/edition/bundle-key.model';
import {BundleKeysPage} from "../../model/edition/bundle-keys-page.model";
import {BundleKeyTranslation} from "../../model/edition/bundle-key-translation.model";

@Component({
    selector: 'app-translations-table',
    templateUrl: './translations-table.component.html',
    styleUrls: ['./translations-table.component.css']
})
export class TranslationsTableComponent implements OnInit {

    @Input()
    private _searchRequest: TranslationsSearchRequest = new TranslationsSearchRequest();

    columns: ColumnDefinition[] = [];
    displayedColumns: string[] = [];
    dataSource: (BundleFile | BundleKey)[] = [];

    constructor(private translationsService: TranslationsService) {
    }

    ngOnInit() {
    }

    get searchRequest(): TranslationsSearchRequest {
        return this._searchRequest;
    }

    @Input()
    set searchRequest(value: TranslationsSearchRequest) {
        this._searchRequest = value;

        if (this.searchRequest != null && this.searchRequest.workspace != null) {
            this.translationsService
                .getTranslations(this.searchRequest)
                .toPromise()
                .then((page: BundleKeysPage) => {
                    this.updateDataSource(page);
                    this.updateColumns();
                });
        }
    }

    isBundleFile(index, item): boolean {
        return item instanceof BundleFile;
    }

    private updateDataSource(page: BundleKeysPage) {
        this.dataSource = [];
        for (const file of page.files) {
            this.dataSource.push(file);

            for (const key of file.keys) {
                this.dataSource.push(key);
            }
        }
    }

    private updateColumns() {
        this.columns = [];
        this.columns.push(
            new ColumnDefinition(
                'key',
                'Key',
                (bundleKey: BundleKey) => `${bundleKey.key}`,
                (bundleKey: BundleKey) => CellType.HEADER
            )
        );

        for (const locale of this._searchRequest.usedLocales()) {
            this.columns.push(
                new ColumnDefinition(
                    locale.toString(),
                    locale,
                    (bundleKey: BundleKey) => bundleKey.findTranslation(locale),
                    (bundleKey: BundleKey) => bundleKey.findTranslation(locale) != null ? CellType.TRANSLATION : CellType.EMPTY
                )
            );
        }

        this.displayedColumns = this.columns.map(column => column.columnDef);
    }
}

export class ColumnDefinition {
    columnDef: string;
    header: string;
    cell: (BundleKey) => BundleKeyTranslation | string;
    cellType: (BundleKey) => CellType;

    constructor(columnDef: string,
                header: string,
                cell: (BundleKey) => (BundleKeyTranslation | string),
                cellType: (BundleKey) => CellType) {
        this.columnDef = columnDef;
        this.header = header;
        this.cell = cell;
        this.cellType = cellType;
    }

    isEmpty(bundleKey: BundleKey): boolean {
        return this.cellType(bundleKey) == CellType.EMPTY;
    }

    isTranslation(bundleKey: BundleKey): boolean {
        return this.cellType(bundleKey) == CellType.TRANSLATION;
    }

    isHeader(bundleKey: BundleKey): boolean {
        return this.cellType(bundleKey) == CellType.HEADER;
    }
}

export enum CellType {

    HEADER = "header",

    TRANSLATION = "translation",

    EMPTY = "empty"
}
