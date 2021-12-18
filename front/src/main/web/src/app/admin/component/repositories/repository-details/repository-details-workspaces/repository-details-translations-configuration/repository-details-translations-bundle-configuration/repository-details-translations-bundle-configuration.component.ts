import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import * as _ from 'lodash';
import { BundleConfiguration } from '@i18n-core-translation';

@Component({
  selector: 'app-repository-details-translations-bundle-configuration',
  templateUrl: './repository-details-translations-bundle-configuration.component.html',
  styleUrls: ['./repository-details-translations-bundle-configuration.component.css'],
})
export class RepositoryDetailsTranslationsBundleConfigurationComponent {
  @ViewChild('newIgnorePathElement') newIgnorePathElement: ElementRef;

  readonly dataSource = new MatTableDataSource<string>();

  private _form: FormGroup;
  private _bundleConfiguration: BundleConfiguration;

  constructor(private _formBuilder: FormBuilder) {}

  static getIgnoredPaths(form: FormGroup): string[] {
    let ignoredPathsForm = <FormArray>form.controls['ignoredPaths'];

    return _.map(ignoredPathsForm.controls, (control: FormControl) => control.value);
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
  get bundleConfiguration(): BundleConfiguration {
    return this._bundleConfiguration;
  }

  set bundleConfiguration(bundleConfiguration: BundleConfiguration) {
    this._bundleConfiguration = bundleConfiguration;

    this._updateForm();
  }

  get ignoredPathsForm(): FormArray {
    return <FormArray>this.form.controls['ignoredPaths'];
  }

  get ignoredPaths(): string[] {
    return _.map(this.ignoredPathsForm.controls, (control) => <string>control.value);
  }

  get newIgnoredPathForm(): FormControl {
    return <FormControl>this.form.controls['newIgnoredPath'];
  }

  get newIgnoredPath(): string {
    return this.newIgnoredPathForm.value;
  }

  onDelete(ignoredPath: string) {
    let index = this._findIndexIgnoredPath(ignoredPath);

    if (index >= 0) {
      this.ignoredPathsForm.removeAt(index);
      this._updateIgnoredPathsDataSource();
    }
  }

  onAdd() {
    if (this.addDisabled) {
      return;
    }

    this.ignoredPathsForm.push(this._formBuilder.control(this.newIgnoredPath));
    this.newIgnoredPathForm.setValue(null);
    this._updateIgnoredPathsDataSource();

    this.newIgnorePathElement.nativeElement.scrollIntoView({ behavior: 'smooth' });
  }

  get addDisabled(): boolean {
    return _.isEmpty(this.newIgnoredPath) || this._findIndexIgnoredPath(this.newIgnoredPath) >= 0;
  }

  private _updateIgnoredPathsDataSource() {
    this.dataSource.data = _.concat(this.ignoredPaths, [null]);
  }

  private _findIndexIgnoredPath(path: string): number {
    return _.findIndex(this.ignoredPaths, (ignoredPath) => _.eq(path, ignoredPath));
  }

  private _updateForm() {
    if (!this.form.controls['ignoredPaths']) {
      this.form.addControl('ignoredPaths', this._formBuilder.array([]));
    }

    if (!this.form.controls['newIgnoredPath']) {
      this.form.addControl('newIgnoredPath', this._formBuilder.control(null));
    }

    if (this.bundleConfiguration) {
      this._fillFormArray(this.bundleConfiguration.ignoredPaths, this.ignoredPathsForm);
    }
  }

  private _fillFormArray(values: string[], formArray: FormArray) {
    formArray.clear();

    _.forEach(values, (value) => formArray.push(this._formBuilder.control(value)));

    formArray.markAsPristine();

    this._updateIgnoredPathsDataSource();
  }
}
