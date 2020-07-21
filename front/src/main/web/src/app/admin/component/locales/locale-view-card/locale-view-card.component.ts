import {Component, Input, OnInit} from '@angular/core';
import {TranslationLocale} from "../../../../translations/model/translation-locale.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {TranslationLocaleService} from "../../../../translations/service/translation-locale.service";
import {NotificationService} from "../../../../core/notification/service/notification.service";

@Component({
    selector: 'app-locale-view-card',
    templateUrl: './locale-view-card.component.html',
    styleUrls: ['./locale-view-card.component.css']
})
export class LocaleViewCardComponent implements OnInit {

    public readonly form: FormGroup;
    public loading: boolean = false;

    private _locale: TranslationLocale;

    constructor(private formBuilder: FormBuilder,
                private translationLocaleService: TranslationLocaleService,
                private notificationService: NotificationService) {
        this.form = this.formBuilder.group(
            {
                displayName: this.formBuilder.control('', []),
                language: this.formBuilder.control('', [Validators.required]),
                region: this.formBuilder.control('', []),
                variants: this.formBuilder.control('', []),
                icon: this.formBuilder.control('', [Validators.required]),
            }
        );
    }

    ngOnInit() {
    }

    @Input()
    public get locale() {
        return this._locale;
    }

    public set locale(value: TranslationLocale) {
        this._locale = value;

        this.resetForm();
    }

    public get displayName(): string {
        return this.form.controls['displayName'].value;
    }

    public get icon(): string {
        return this.form.controls['icon'].value;
    }

    public get iconClass(): string {
        return `flag-icon ${this.form.controls['icon'].value}`;
    }

    public onSave() {
        this.loading = true;

        this.translationLocaleService
            .updateLocale(this.toLocale())
            .toPromise()
            .catch(error => {
                console.error('Error while saving language.', error);
                this.notificationService.displayErrorMessage("Error while saving language.");
            })
            .finally(() => this.loading = false);
    }

    public resetForm() {
        this.form.controls['displayName'].setValue(this.locale.displayName);
        this.form.controls['language'].setValue(this.locale.language);
        this.form.controls['region'].setValue(this.locale.region);
        this.form.controls['variants'].setValue(this.locale.variants);
        this.form.controls['icon'].setValue(this.locale.icon);

        this.form.markAsPristine();
    }

    private toLocale(): TranslationLocale {
        return new TranslationLocale(
            this.locale.id,
            this.form.controls['language'].value,
            this.form.controls['icon'].value,
            this.form.controls['displayName'].value,
            this.form.controls['region'].value,
            this.form.controls['variants'].value // TODO
        );
    }
}
