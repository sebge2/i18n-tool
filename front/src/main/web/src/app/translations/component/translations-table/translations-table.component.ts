import {Component, Input, OnInit} from '@angular/core';
import {TranslationsSearchRequest} from "../../model/translations-search-request.model";
import {TranslationsService} from '../../service/translations.service';
import {BundleKeysPage} from "../../model/edition/bundle-keys-page.model";
import {FormArray, FormBuilder, FormGroup} from "@angular/forms";
import {auditTime} from "rxjs/operators";
import {Locale} from "../../model/locale.model";
import {BundleKey} from "../../model/edition/bundle-key.model";

@Component({
    selector: 'app-translations-table',
    templateUrl: './translations-table.component.html',
    styleUrls: ['./translations-table.component.css']
})
export class TranslationsTableComponent implements OnInit {

    @Input()
    private _searchRequest: TranslationsSearchRequest = new TranslationsSearchRequest();

    columnDefinitions: ColumnDefinition[] = [];
    displayedColumns: string[] = [];
    form: FormArray;

    constructor(private translationsService: TranslationsService,
                private formBuilder: FormBuilder) {
        this.form = formBuilder.array([]);
    }

    ngOnInit() {
        this.form.valueChanges
            .pipe(
                auditTime(2000)
            )
            .subscribe((formData: FormData) => {
                // TODO
                console.log(formData);
            });
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
                .then(
                    (page: BundleKeysPage) => {
                        this.form.clear();

                        this.updateColumnDefinitions();
                        this.updateForm(page);
                    }
                );
        }
    }

    isBundleFile(index, item): boolean {
        return item instanceof FormGroup;
    }

    private updateForm(page: BundleKeysPage) {
        for (const file of page.files) {
            this.form.push(
                this.formBuilder.group({file})
            );

            for (const key of file.keys) {
                const keyFormArray = this.formBuilder.array([]);

                this.form.push(keyFormArray);

                keyFormArray.push(
                    this.formBuilder.group({key})
                );

                for (const translation of key.translations) {
                    keyFormArray.push(
                        this.formBuilder.group({
                            translation,
                            value: [translation.currentValue()]
                        })
                    );
                }
            }
        }
    }

    private updateColumnDefinitions() {
        this.columnDefinitions = [];
        this.columnDefinitions.push(
            new ColumnDefinition(
                'key',
                'Key',
                (formArray: FormArray) => `${formArray.controls[0].value.key.key}`,
                (formArray: FormArray) => CellType.KEY
            )
        );

        for (let i = 0; i < this._searchRequest.usedLocales().length; i++) {
            const locale: Locale = this._searchRequest.usedLocales()[i];

            this.columnDefinitions.push(
                new ColumnDefinition(
                    locale.toString(),
                    locale,
                    (formArray: FormArray) => <FormGroup>formArray.controls[i+1],
                    (formArray: FormArray) =>
                        (<BundleKey>(<FormGroup>formArray.controls[0]).value.key).findTranslation(locale) != null
                            ? CellType.TRANSLATION : CellType.EMPTY
                )
            );
        }

        this.displayedColumns = this.columnDefinitions.map(column => column.columnDef);
    }
}

export class ColumnDefinition {
    columnDef: string;
    header: string;
    cell: (FormArray) => (FormGroup | string);
    cellType: (FormArray) => CellType;

    constructor(columnDef: string,
                header: string,
                cell: (FormArray) => (FormGroup | string),
                cellType: (FormArray) => CellType) {
        this.columnDef = columnDef;
        this.header = header;
        this.cell = cell;
        this.cellType = cellType;
    }

    isEmpty(formArray: FormArray): boolean {
        return this.cellType(formArray) == CellType.EMPTY;
    }

    isTranslation(formArray: FormArray): boolean {
        return this.cellType(formArray) == CellType.TRANSLATION;
    }

    isKey(formArray: FormArray): boolean {
        return this.cellType(formArray) == CellType.KEY;
    }
}

export enum CellType {

    KEY = "key",

    TRANSLATION = "translation",

    EMPTY = "empty"
}
