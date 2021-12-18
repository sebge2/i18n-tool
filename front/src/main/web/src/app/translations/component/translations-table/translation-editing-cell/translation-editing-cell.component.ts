import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { TranslationsPageRow } from '../../../model/search/translations-page-row.model';
import { WorkspaceService } from '@i18n-core-translation';
import { map, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import * as _ from 'lodash';

@Component({
  selector: 'app-translation-editing-cell',
  templateUrl: './translation-editing-cell.component.html',
  styleUrls: ['./translation-editing-cell.component.css'],
})
export class TranslationEditingCellComponent implements OnInit, OnDestroy {
  private _form: FormGroup;
  private readonly _destroyed$ = new Subject<void>();

  constructor(private _workspaceService: WorkspaceService) {}

  ngOnInit() {
    this._workspaceService
      .getWorkspace(this.pageRow.workspace)
      .pipe(
        takeUntil(this._destroyed$),
        map((workspace) => workspace && !workspace.isInReview())
      )
      .subscribe((enabled) => (enabled ? this.form.enable() : this.form.disable()));
  }

  ngOnDestroy(): void {
    this._destroyed$.next();
    this._destroyed$.complete();
  }

  @Input()
  get form(): FormGroup {
    return this._form;
  }

  set form(form: FormGroup) {
    this._form = form;
  }

  onReset() {
    this.form.controls['value'].setValue(this.originalValue);
    this.form.controls['value'].markAsDirty();
  }

  get originalValue(): string {
    return this.form.controls['originalValue'].value;
  }

  get value(): string {
    return this.form.controls['value'].value;
  }

  get pageRow(): TranslationsPageRow {
    return this.form.controls['pageRow'].value;
  }

  get cancelDisabled(): boolean {
    return _.eq(this.value, this.originalValue);
  }
}
