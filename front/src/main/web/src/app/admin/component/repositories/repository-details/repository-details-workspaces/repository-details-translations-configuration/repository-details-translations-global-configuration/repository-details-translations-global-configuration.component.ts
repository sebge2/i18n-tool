import {Component, ElementRef, Input, ViewChild} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {FormArray, FormBuilder, FormControl, FormGroup} from "@angular/forms";
import * as _ from "lodash";
import {TranslationsConfiguration} from "../../../../../../../translations/model/repository/translations-configuration.model";

@Component({
    selector: 'app-repository-details-translations-global-configuration',
    templateUrl: './repository-details-translations-global-configuration.component.html',
    styleUrls: ['./repository-details-translations-global-configuration.component.css']
})
export class RepositoryDetailsTranslationsGlobalConfigurationComponent {

    @ViewChild('newIgnoreKeyElement') newIgnoreKeyElement: ElementRef;

    public readonly dataSource = new MatTableDataSource<string>();

    private _form: FormGroup;
    private _translationsConfiguration: TranslationsConfiguration;

    constructor(private _formBuilder: FormBuilder) {
    }

    public static getIgnoredProperties(form: FormGroup): string[] {
        let ignoredKeysForm = <FormArray>form.controls['ignoredKeys'];

        return _.map(ignoredKeysForm.controls, (control: FormControl) => control.value);
    }

    @Input()
    public get form(): FormGroup {
        return this._form;
    }

    public set form(form: FormGroup) {
        this._form = form;

        this.updateForm();
    }

    @Input()
    public get translationsConfiguration(): TranslationsConfiguration {
        return this._translationsConfiguration;
    }

    public set translationsConfiguration(translationsConfiguration: TranslationsConfiguration) {
        this._translationsConfiguration = translationsConfiguration;

        this.updateForm();
    }

    public get ignoredKeysForm(): FormArray {
        return (<FormArray>this.form.controls['ignoredKeys']);
    }

    public get ignoredKeys(): string[] {
        return _.map(this.ignoredKeysForm.controls, control => <string>control.value);
    }

    public get newIgnoredKeyForm(): FormControl {
        return <FormControl>this.form.controls['newIgnoredKey'];
    }

    public get newIgnoredKey(): string {
        return this.newIgnoredKeyForm.value;
    }

    public onDelete(ignoredKey: string) {
        let index = this.findIndexIgnoredKey(ignoredKey);

        if (index >= 0) {
            this.ignoredKeysForm.removeAt(index);
            this.updateIgnoredKeysDataSource();
        }
    }

    public onAdd() {
        if (this.addDisabled) {
            return;
        }

        this.ignoredKeysForm.push(this._formBuilder.control(this.newIgnoredKey));
        this.newIgnoredKeyForm.setValue(null);
        this.updateIgnoredKeysDataSource();

        this.newIgnoreKeyElement.nativeElement.scrollIntoView({behavior: 'smooth'})
    }

    public get addDisabled(): boolean {
        return _.isEmpty(this.newIgnoredKey) || this.findIndexIgnoredKey(this.newIgnoredKey) >= 0;
    }

    private updateIgnoredKeysDataSource() {
        this.dataSource.data = _.concat(this.ignoredKeys, [null]);
    }

    private findIndexIgnoredKey(key: string): number {
        return _.findIndex(this.ignoredKeys, ignoredKey => _.eq(key, ignoredKey));
    }

    private updateForm() {
        if (!this.form.controls['ignoredKeys']) {
            this.form.addControl('ignoredKeys', this._formBuilder.array([]));
        }

        if (!this.form.controls['newIgnoredKey']) {
            this.form.addControl('newIgnoredKey', this._formBuilder.control(null));
        }

        if (this.translationsConfiguration) {
            this.fillFormArray(this.translationsConfiguration.ignoredKeys, this.ignoredKeysForm);
        }
    }

    private fillFormArray(values: string[], formArray: FormArray) {
        formArray.clear();

        _.forEach(values, value => formArray.push(this._formBuilder.control(value)));

        formArray.markAsPristine();

        this.updateIgnoredKeysDataSource();
    }
}
