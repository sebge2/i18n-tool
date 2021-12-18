import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { TranslationService } from '@i18n-core-translation';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { from, Observable, Subject } from 'rxjs';
import { NotificationService } from '@i18n-core-notification';
import { WorkspaceService } from '@i18n-core-translation';
import { BundleFile } from '@i18n-core-translation';
import * as _ from 'lodash';
import { Workspace } from '@i18n-core-translation';
import { TranslationsPage } from '../../model/search/translations-page.model';
import { TranslationsPageRow } from '../../model/search/translations-page-row.model';
import { auditTime, map, mergeMap, takeUntil, tap } from 'rxjs/operators';
import { TranslationUpdateDto } from '../../../api';
import {
  EnrichedTranslationsSearchRequest,
  TranslationsTableState,
} from '../../model/search/translation-search-state.model';

class DirtyTranslationForm {
  constructor(
    public bundleKeyId: string,
    public localeId: string,
    public translation: string,
    public translationForm: FormGroup
  ) {}
}

export enum RowType {
  WORKSPACE = 'WORKSPACE',

  BUNDLE_FILE = 'BUNDLE_FILE',

  BUNDLE_KEY = 'BUNDLE_KEY',
}

@Component({
  selector: 'app-translations-table',
  templateUrl: './translations-table.component.html',
  styleUrls: ['./translations-table.component.scss'],
})
export class TranslationsTableComponent implements OnInit, OnDestroy {
  static readonly PAGE_SIZE: number = 500;

  @Input() state: TranslationsTableState = new TranslationsTableState();

  readonly form: FormArray;
  RowType = RowType;

  private readonly _destroyed$ = new Subject<void>();

  constructor(
    private _formBuilder: FormBuilder,
    private _translationService: TranslationService,
    private _notificationService: NotificationService,
    private _workspaceService: WorkspaceService
  ) {
    this.form = _formBuilder.array([]);
  }

  ngOnInit(): void {
    this.form.valueChanges.pipe(takeUntil(this._destroyed$), auditTime(5000)).subscribe(() => {
      this.state.notifySaving(true);
      const dirtyTranslations = this._findDirtyTranslations();

      this._translationService
        .updateTranslations(
          dirtyTranslations.map(
            (dirtyTranslation) =>
              <TranslationUpdateDto>{
                bundleKeyId: dirtyTranslation.bundleKeyId,
                localeId: dirtyTranslation.localeId,
                translation: dirtyTranslation.translation,
              }
          )
        )
        .toPromise()
        .then(() => this._updateDirtyFlags(dirtyTranslations))
        .catch((error) => {
          console.error('Error while updating translations.', error);
          this._notificationService.displayErrorMessage('TRANSLATIONS.ERROR.UPDATE', error);
        })
        .finally(() => this.state.notifySaving(false));
    });

    this.form.valueChanges
      .pipe(
        takeUntil(this._destroyed$),
        map(() => this.form.dirty)
      )
      .subscribe((dirty) => this.state.notifyUnsavedChanges(dirty));

    this.state.searchRequest
      .pipe(
        takeUntil(this._destroyed$),
        tap(() => this.state.notifyLoading(true)),
        mergeMap((enrichedRequest: EnrichedTranslationsSearchRequest) =>
          from(
            this._translationService
              .searchTranslations(enrichedRequest.request, TranslationsTableComponent.PAGE_SIZE)
              .toPromise()
              .then((page) => ({ page: page, origin: enrichedRequest.origin }))
          )
        )
      )
      .subscribe(
        (enrichedPage: { page: TranslationsPage; origin: 'NEW' | 'NEXT' | 'PREVIOUS' }) => {
          if (enrichedPage.page.hasRows) {
            this._updateSource(enrichedPage.page);

            this.state.notifyLoading(false);

            this.state.updatePage(enrichedPage.page);
          } else {
            this.state.notifyLoading(false);

            if (enrichedPage.origin === 'NEW') {
              this.state.updatePage(enrichedPage.page);
            } else if (enrichedPage.origin === 'NEXT') {
              this._notificationService.displayInfoMessage('TRANSLATIONS.TABLE.NO_FURTHER_TRANSLATION');
            } else if (enrichedPage.origin === 'PREVIOUS') {
              this._notificationService.displayInfoMessage('TRANSLATIONS.TABLE.NO_PREVIOUS_TRANSLATION');
            }
          }
        },
        (error) => {
          this.state.notifyLoading(false);
          console.error('Error while searching for translations.', error);
          this._notificationService.displayErrorMessage('TRANSLATIONS.ERROR.SEARCH', error);
        }
      );
  }

  ngOnDestroy(): void {
    this._destroyed$.next();
    this._destroyed$.complete();
  }

  get spreadRowClass(): string {
    return `app-scroller-spread-row-${this.state.newSearchRequestSync.locales.length}`;
  }

  get rows(): FormGroup[] {
    return <FormGroup[]>this.form.controls;
  }

  getRowType(row: FormGroup): RowType {
    return row.controls['type'].value;
  }

  getWorkspaceId(row: FormGroup): string {
    return row.controls['workspace'].value;
  }

  getWorkspace(rowForm: FormGroup): Observable<Workspace | undefined> {
    return this._workspaceService.getWorkspace(this.getWorkspaceId(rowForm));
  }

  getBundleFileId(row: FormGroup): string {
    return row.controls['bundleFile'].value;
  }

  getBundleFile(rowForm: FormGroup): Observable<BundleFile | undefined> {
    const workspace = this.getWorkspaceId(rowForm);
    const bundleFile = this.getBundleFileId(rowForm);

    return this._workspaceService.getWorkspaceBundleFile(workspace, bundleFile);
  }

  getBundleKeyId(row: FormGroup): string {
    return row.controls['bundleKeyId'].value;
  }

  getBundleKey(row: FormGroup): string {
    return row.controls['bundleKey'].value;
  }

  getRowTranslations(row: FormGroup): FormGroup[] {
    return <FormGroup[]>(<FormArray>row.controls['translations']).controls;
  }

  getTranslationValue(translationForm: FormGroup): string {
    return translationForm.controls['value'].value;
  }

  // noinspection JSUnusedLocalSymbols
  trackByFn(indexInSource, index, item) {
    return indexInSource;
  }

  private _updateSource(page: TranslationsPage) {
    this.form.clear();

    for (const pageRow of page.rows) {
      const currentRow = this._createBundleKeyRow(pageRow);

      if (_.some(this.form.controls)) {
        const lastRow = <FormGroup>_.last(this.form.controls);

        if (!_.isEqual(this.getWorkspaceId(lastRow), this.getWorkspaceId(currentRow))) {
          this.form.push(this._createWorkspaceRow(pageRow));
        }

        if (!_.isEqual(this.getBundleFileId(lastRow), this.getBundleFileId(currentRow))) {
          this.form.push(this._createBundleFileRow(pageRow));
        }
      } else {
        this.form.push(this._createWorkspaceRow(pageRow));
        this.form.push(this._createBundleFileRow(pageRow));
      }

      this.form.push(currentRow);
    }
  }

  private _createWorkspaceRow(pageRow: TranslationsPageRow): FormGroup {
    return this._formBuilder.group({
      type: this._formBuilder.control(RowType.WORKSPACE),
      workspace: this._formBuilder.control(pageRow.workspace),
      bundleFile: this._formBuilder.control(pageRow.bundleFile),
    });
  }

  private _createBundleFileRow(pageRow: TranslationsPageRow): FormGroup {
    return this._formBuilder.group({
      type: this._formBuilder.control(RowType.BUNDLE_FILE),
      workspace: this._formBuilder.control(pageRow.workspace),
      bundleFile: this._formBuilder.control(pageRow.bundleFile),
    });
  }

  private _createBundleKeyRow(pageRow: TranslationsPageRow): FormGroup {
    return this._formBuilder.group({
      type: this._formBuilder.control(RowType.BUNDLE_KEY),
      workspace: this._formBuilder.control(pageRow.workspace),
      bundleFile: this._formBuilder.control(pageRow.bundleFile),
      bundleKeyId: this._formBuilder.control(pageRow.bundleKeyId),
      bundleKey: this._formBuilder.control(pageRow.bundleKey),
      translations: this._formBuilder.array(
        _.range(0, this.state.newSearchRequestSync.locales.length).map((i) => this._createBundleKeyCell(pageRow, i))
      ),
    });
  }

  private _createBundleKeyCell(pageRow: TranslationsPageRow, index: number): FormGroup {
    const translationsPageTranslation = pageRow.translations[index];

    return this._formBuilder.group({
      value: this._formBuilder.control(
        translationsPageTranslation.updatedValue
          ? translationsPageTranslation.updatedValue
          : translationsPageTranslation.originalValue
      ),
      originalValue: this._formBuilder.control(translationsPageTranslation.originalValue),
      pageRow: pageRow,
    });
  }

  private _findDirtyTranslations(): DirtyTranslationForm[] {
    return <DirtyTranslationForm[]>_.flatten(
      this.form.controls
        .filter((rowForm) => rowForm.dirty)
        .map((rowForm: FormGroup) => {
          const translationForms = this.getRowTranslations(rowForm);

          return translationForms
            .filter((control) => control.dirty)
            .map(
              (translationForm: FormGroup) =>
                new DirtyTranslationForm(
                  this.getBundleKeyId(rowForm),
                  this.state.newSearchRequestSync.locales[translationForms.indexOf(translationForm)].id,
                  this.getTranslationValue(translationForm),
                  translationForm
                )
            );
        })
    );
  }

  private _updateDirtyFlags(dirtyTranslations: DirtyTranslationForm[]) {
    dirtyTranslations.forEach((dirtyTranslation) => {
      if (_.eq(dirtyTranslation.translation, this.getTranslationValue(dirtyTranslation.translationForm))) {
        dirtyTranslation.translationForm.markAsPristine();
      }
    });

    this.state.notifyUnsavedChanges(this.form.dirty);
  }
}
