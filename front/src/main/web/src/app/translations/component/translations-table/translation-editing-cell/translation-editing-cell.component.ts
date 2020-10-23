import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {TranslationsPageRow} from "../../../model/search/translations-page-row.model";
import {WorkspaceService} from "../../../service/workspace.service";
import {map, takeUntil} from "rxjs/operators";
import {Subject} from "rxjs";
import * as _ from "lodash";

@Component({
    selector: 'app-translation-editing-cell',
    templateUrl: './translation-editing-cell.component.html',
    styleUrls: ['./translation-editing-cell.component.css']
})
export class TranslationEditingCellComponent implements OnInit, OnDestroy {

    private _form: FormGroup;
    private _destroyed$ = new Subject<void>();

    constructor(private _workspaceService: WorkspaceService) {
    }

    public ngOnInit() {
        this._workspaceService
            .getWorkspace(this.pageRow.workspace)
            .pipe(
                takeUntil(this._destroyed$),
                map(workspace => !workspace.isInReview())
            )
            .subscribe(enabled => enabled ? this.form.enable() : this.form.disable());
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    @Input()
    public get form(): FormGroup {
        return this._form;
    }

    public set form(form: FormGroup) {
        this._form = form;
    }

    public onReset() {
        this.form.controls['value'].setValue(this.originalValue);
        this.form.controls['value'].markAsDirty();
    }

    public get originalValue(): string {
        return this.form.controls['originalValue'].value;
    }

    public get value(): string {
        return this.form.controls['value'].value;
    }

    public get pageRow(): TranslationsPageRow {
        return this.form.controls['pageRow'].value;
    }

    public get cancelDisabled(): boolean {
        return _.eq(this.value, this.originalValue);
    }
}
