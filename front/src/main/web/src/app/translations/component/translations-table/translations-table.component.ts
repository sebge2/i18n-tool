import {Component, Input, OnInit} from '@angular/core';
import {TranslationsSearchRequest} from "../../model/translations-search-request.model";
import {TranslationsService} from '../../service/translations.service';
import {BundleKeysPage} from "../../model/edition/bundle-keys-page.model";
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {auditTime} from "rxjs/operators";
import {Locale} from "../../model/locale.model";
import {BundleKey} from "../../model/edition/bundle-key.model";
import {BundleKeyTranslation} from "../../model/edition/bundle-key-translation.model";
import {ColumnDefinition} from "../../model/table/column-definition.model";
import {CellType} from "../../model/table/cell-type.model";
import {TranslationsStartReviewComponent, StartReviewDialogModel} from "./translations-start-review/translations-start-review.component";
import {MatDialog} from "@angular/material";

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
                private formBuilder: FormBuilder,
                private dialog: MatDialog) {
        this.form = formBuilder.array([]);
    }

    ngOnInit() {
        this.subscribe();
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
                        this.form = this.formBuilder.array([]); // TODO better for clearing ? remove this.subscribe() too

                        // // TODO
                        // if(this.searchRequest.workspace.status == WorkspaceStatus.IN_REVIEW){
                        //     this.form.disable();
                        // }

                        this.updateColumnDefinitions();
                        this.updateForm(page);
                        this.subscribe();
                    }
                );
        }
    }

    isBundleFile(index, item): boolean {
        return item instanceof FormGroup;
    }

    openStartReviewDialog(): void {
        this.dialog
            .open(TranslationsStartReviewComponent, {
                width: '250px',
                data: <StartReviewDialogModel> {comment: ""}
            })
            .afterClosed()
            .subscribe((result: StartReviewDialogModel) => {
                if(result){
                    console.log('The dialog was closed', result.comment);
                }
            });
    }

    private subscribe() {
        // TODO subscription
        this.form.valueChanges
            .pipe(auditTime(2000))
            .subscribe((formData: AbstractControl[]) => {
                const updatedTranslations: Map<string, string> = new Map<string, string>();

                this.form.controls
                    .filter(control => control instanceof FormArray)
                    .filter(bundleKeyFormArray => bundleKeyFormArray.dirty)
                    .map((bundleKeyFormArray: FormArray) =>
                        bundleKeyFormArray.controls
                            .filter(bundleKeyControl => bundleKeyControl.dirty && bundleKeyControl.valid)
                            .map(
                                (formGroup: FormGroup) => {
                                    updatedTranslations.set(formGroup.value.translation.id, formGroup.value.value);
                                    formGroup.get("value").reset(formGroup.value.value);
                                }
                            )
                    );

                // TODO only if focus lost
                this.translationsService
                    .updateTranslations(this._searchRequest.workspace.id, updatedTranslations)
                /*.catch(result => this.form.markAsDirty()) TODO fix this*/;
            });
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

                for (let i = 0; i < this._searchRequest.usedLocales().length; i++) {
                    const translation: BundleKeyTranslation = key.findTranslation(this._searchRequest.usedLocales()[i]);
                    const formGroup = this.formBuilder.group({translation});

                    formGroup.addControl(
                        'value',
                        this.formBuilder.control((translation != null) ? translation.currentValue() : null, [Validators.required])
                    );

                    keyFormArray.push(formGroup);
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
                    (formArray: FormArray) => <FormGroup>formArray.controls[i + 1],
                    (formArray: FormArray) =>
                        (<BundleKey>(<FormGroup>formArray.controls[0]).value.key).findTranslation(locale) != null
                            ? CellType.TRANSLATION : CellType.EMPTY
                )
            );
        }

        this.displayedColumns = this.columnDefinitions.map(column => column.columnDef);
    }
}
