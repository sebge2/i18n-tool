import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import * as _ from 'lodash';
import { TranslationsConfiguration } from '@i18n-core-translation';

@Component({
  selector: 'app-repository-details-translations-global-configuration',
  templateUrl: './repository-details-translations-global-configuration.component.html',
  styleUrls: ['./repository-details-translations-global-configuration.component.css'],
})
export class RepositoryDetailsTranslationsGlobalConfigurationComponent {
  @ViewChild('newIgnoreKeyElement') newIgnoreKeyElement: ElementRef;

  readonly dataSource = new MatTableDataSource<string>();

  private _form: FormGroup;
  private _translationsConfiguration: TranslationsConfiguration;

  constructor(private _formBuilder: FormBuilder) {}

  static getIgnoredProperties(form: FormGroup): string[] {
    let ignoredKeysForm = <FormArray>form.controls['ignoredKeys'];

    return _.map(ignoredKeysForm.controls, (control: FormControl) => control.value);
  }

  @Input()
  get form(): FormGroup {
    return this._form;
  }

  set form(form: FormGroup) {
    this._form = form;

    this._updateForm();
  }

  @Input()
  get translationsConfiguration(): TranslationsConfiguration {
    return this._translationsConfiguration;
  }

  set translationsConfiguration(translationsConfiguration: TranslationsConfiguration) {
    this._translationsConfiguration = translationsConfiguration;

    this._updateForm();
  }

  get ignoredKeysForm(): FormArray {
    return <FormArray>this.form.controls['ignoredKeys'];
  }

  get ignoredKeys(): string[] {
    return _.map(this.ignoredKeysForm.controls, (control) => <string>control.value);
  }

  get newIgnoredKeyForm(): FormControl {
    return <FormControl>this.form.controls['newIgnoredKey'];
  }

  get newIgnoredKey(): string {
    return this.newIgnoredKeyForm.value;
  }

  onDelete(ignoredKey: string) {
    let index = this._findIndexIgnoredKey(ignoredKey);

    if (index >= 0) {
      this.ignoredKeysForm.removeAt(index);
      this._updateIgnoredKeysDataSource();
    }
  }

  onAdd() {
    if (this.addDisabled) {
      return;
    }

    this.ignoredKeysForm.push(this._formBuilder.control(this.newIgnoredKey));
    this.newIgnoredKeyForm.setValue(null);
    this._updateIgnoredKeysDataSource();

    this.newIgnoreKeyElement.nativeElement.scrollIntoView({ behavior: 'smooth' });
  }

  get addDisabled(): boolean {
    return _.isEmpty(this.newIgnoredKey) || this._findIndexIgnoredKey(this.newIgnoredKey) >= 0;
  }

  private _updateIgnoredKeysDataSource() {
    this.dataSource.data = _.concat(this.ignoredKeys, [null]);
  }

  private _findIndexIgnoredKey(key: string): number {
    return _.findIndex(this.ignoredKeys, (ignoredKey) => _.eq(key, ignoredKey));
  }

  private _updateForm() {
    if (!this.form.controls['ignoredKeys']) {
      this.form.addControl('ignoredKeys', this._formBuilder.array([]));
    }

    if (!this.form.controls['newIgnoredKey']) {
      this.form.addControl('newIgnoredKey', this._formBuilder.control(null));
    }

    if (this.translationsConfiguration) {
      this._fillFormArray(this.translationsConfiguration.ignoredKeys, this.ignoredKeysForm);
    }
  }

  private _fillFormArray(values: string[], formArray: FormArray) {
    formArray.clear();

    _.forEach(values, (value) => formArray.push(this._formBuilder.control(value)));

    formArray.markAsPristine();

    this._updateIgnoredKeysDataSource();
  }
}
