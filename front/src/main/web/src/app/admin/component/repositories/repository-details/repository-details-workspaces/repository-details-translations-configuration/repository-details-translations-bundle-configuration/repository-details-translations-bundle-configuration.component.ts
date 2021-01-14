import {Component, ElementRef, Input, ViewChild} from '@angular/core';
import {FormArray, FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {MatTableDataSource} from "@angular/material/table";
import * as _ from "lodash";
import {BundleConfiguration} from "../../../../../../../translations/model/repository/bundle-configuration.model";

@Component({
    selector: 'app-repository-details-translations-bundle-configuration',
    templateUrl: './repository-details-translations-bundle-configuration.component.html',
    styleUrls: ['./repository-details-translations-bundle-configuration.component.css']
})
export class RepositoryDetailsTranslationsBundleConfigurationComponent {

    @ViewChild('newIgnorePathElement') newIgnorePathElement: ElementRef;

    public readonly dataSource = new MatTableDataSource<string>();

    private _form: FormGroup;
    private _bundleConfiguration: BundleConfiguration;

    constructor(private _formBuilder: FormBuilder) {
    }

    public static getIgnoredPaths(form: FormGroup): string[] {
        let ignoredPathsForm = <FormArray>form.controls['ignoredPaths'];

        return _.map(ignoredPathsForm.controls, (control: FormControl) => control.value);
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
    public get bundleConfiguration(): BundleConfiguration {
        return this._bundleConfiguration;
    }

    public set bundleConfiguration(bundleConfiguration: BundleConfiguration) {
        this._bundleConfiguration = bundleConfiguration;

        this.updateForm();
    }

    public get ignoredPathsForm(): FormArray {
        return (<FormArray>this.form.controls['ignoredPaths']);
    }

    public get ignoredPaths(): string[] {
        return _.map(this.ignoredPathsForm.controls, control => <string>control.value);
    }

    public get newIgnoredPathForm(): FormControl {
        return <FormControl>this.form.controls['newIgnoredPath'];
    }

    public get newIgnoredPath(): string {
        return this.newIgnoredPathForm.value;
    }

    public onDelete(ignoredPath: string) {
        let index = this.findIndexIgnoredPath(ignoredPath);

        if (index >= 0) {
            this.ignoredPathsForm.removeAt(index);
            this.updateIgnoredPathsDataSource();
        }
    }

    public onAdd() {
        if (this.addDisabled) {
            return;
        }

        this.ignoredPathsForm.push(this._formBuilder.control(this.newIgnoredPath));
        this.newIgnoredPathForm.setValue(null);
        this.updateIgnoredPathsDataSource();

        this.newIgnorePathElement.nativeElement.scrollIntoView({behavior: 'smooth'})
    }

    public get addDisabled(): boolean {
        return _.isEmpty(this.newIgnoredPath) || this.findIndexIgnoredPath(this.newIgnoredPath) >= 0;
    }

    private updateIgnoredPathsDataSource() {
        this.dataSource.data = _.concat(this.ignoredPaths, [null]);
    }

    private findIndexIgnoredPath(path: string): number {
        return _.findIndex(this.ignoredPaths, ignoredPath => _.eq(path, ignoredPath));
    }

    private updateForm() {
        if (!this.form.controls['ignoredPaths']) {
            this.form.addControl('ignoredPaths', this._formBuilder.array([]));
        }

        if (!this.form.controls['newIgnoredPath']) {
            this.form.addControl('newIgnoredPath', this._formBuilder.control(null));
        }

        if (this.bundleConfiguration) {
            this.fillFormArray(this.bundleConfiguration.ignoredPaths, this.ignoredPathsForm);
        }
    }

    private fillFormArray(values: string[], formArray: FormArray) {
        formArray.clear();

        _.forEach(values, value => formArray.push(this._formBuilder.control(value)));

        formArray.markAsPristine();

        this.updateIgnoredPathsDataSource();
    }

}
