import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {TranslationsSearchRequest} from "../../model/translations-search-request.model";
import {TranslationsService} from '../../service/translations.service';
import {BundleKeysPage} from "../../model/edition/bundle-keys-page.model";
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Locale} from "../../model/locale.model";
import {BundleKey} from "../../model/edition/bundle-key.model";
import {BundleKeyTranslation} from "../../model/edition/bundle-key-translation.model";
import {ColumnDefinition} from "../../model/table/column-definition.model";
import {CellType} from "../../model/table/cell-type.model";
import {MatTable} from "@angular/material";
import {auditTime, takeUntil} from 'rxjs/operators';
import {BehaviorSubject, Subject} from "rxjs";

@Component({
    selector: 'app-translations-table',
    templateUrl: './translations-table.component.html',
    styleUrls: ['./translations-table.component.css']
})
export class TranslationsTableComponent implements OnInit, OnDestroy {

    @Input()
    private _searchRequest: TranslationsSearchRequest = new TranslationsSearchRequest();

    @ViewChild('table', {static: false})
    private table: MatTable<any>;

    columnDefinitions: ColumnDefinition[] = [];
    displayedColumns: string[] = [];
    form: FormArray;

    private destroy$ = new Subject();
    private _readOnly = new BehaviorSubject<boolean>(false);

    constructor(private translationsService: TranslationsService,
                private formBuilder: FormBuilder) {
        this.form = formBuilder.array([]);
    }

    ngOnInit() {
        this.form.valueChanges
            .pipe(
                takeUntil(this.destroy$),
                auditTime(2000)
            )
            .subscribe((formData: AbstractControl[]) => {
                const updatedTranslations: Map<string, string> = new Map<string, string>();

                this.form.controls
                    .filter(control => control instanceof FormArray)
                    .filter(bundleKeyFormArray => bundleKeyFormArray.dirty)
                    .map((bundleKeyFormArray: FormArray) => {
                            bundleKeyFormArray.controls
                                .filter(bundleKeyControl => bundleKeyControl.dirty && bundleKeyControl.valid)
                                .map(
                                    (formGroup: FormGroup) => {
                                        updatedTranslations.set(formGroup.value.translation.id, formGroup.value.value);
                                        formGroup.get("value").reset(formGroup.value.value);
                                    }
                                );
                        }
                    );

                this.translationsService
                    .updateTranslations(this._searchRequest.workspace.id, updatedTranslations)
                /*.catch(result => this.form.markAsDirty()) TODO fix this*/;
            });
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    get searchRequest(): TranslationsSearchRequest {
        return this._searchRequest;
    }

    @Input()
    set searchRequest(value: TranslationsSearchRequest) {
        this._searchRequest = value;

        if (this.searchRequest && this.searchRequest.isValid()) {
            this.translationsService
                .getTranslations(this.searchRequest)
                .toPromise()
                .then(
                    (page: BundleKeysPage) => {
                        this.updateColumnDefinitions();
                        this.updateForm(page);

                        if (this.table) {
                            this.table.renderRows();
                        }
                    }
                );
        }
    }

    get readOnly(): boolean {
        return this.form.disabled;
    }

    @Input()
    set readOnly(readonly: boolean) {
        this._readOnly.next(readonly);
    }

    isBundleFile(index, item): boolean {
        return item instanceof FormGroup;
    }

    private updateForm(page: BundleKeysPage) {
        this.form.clear();

        for (const file of page.files) {
            this.form.push(
                this.formBuilder.group({file})
            );

            for (const key of file.keys) {
                const keyFormArray = this.formBuilder.array([]);

                keyFormArray.push(
                    this.formBuilder.group({key})
                );

                for (let i = 0; i < this._searchRequest.usedLocales().length; i++) {
                    const translation: BundleKeyTranslation = key.findTranslation(this._searchRequest.usedLocales()[i]);

                    const formGroup = this.formBuilder.group({translation});

                    const control = this.formBuilder.control(
                        (translation != null) ? translation.currentValue() : null,
                        [Validators.required]
                    );

                    this._readOnly
                        .pipe(takeUntil(this.destroy$))
                        .subscribe((readOnly: boolean) => {
                            if (readOnly) {
                                control.disable();
                            } else {
                                control.enable();
                            }
                        });

                    formGroup.addControl('value', control);

                    keyFormArray.push(formGroup);
                }

                this.form.push(keyFormArray);
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
                    (formArray: FormArray) => <FormGroup>formArray.controls[i + 1],
                    (formArray: FormArray) => {
                        const bundleKey = <BundleKey>(<FormGroup>formArray.controls[0]).value.key;

                        return (bundleKey).findTranslation(locale) != null
                            ? CellType.TRANSLATION : CellType.EMPTY;
                    }
                )
            );
        }

        this.displayedColumns = this.columnDefinitions.map(column => column.columnDef);
    }
}
