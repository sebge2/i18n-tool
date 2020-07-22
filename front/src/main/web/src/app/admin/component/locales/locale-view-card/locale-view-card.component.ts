import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TranslationLocale} from "../../../../translations/model/translation-locale.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {TranslationLocaleService} from "../../../../translations/service/translation-locale.service";
import {NotificationService} from "../../../../core/notification/service/notification.service";
import * as _ from "lodash";

@Component({
    selector: 'app-locale-view-card',
    templateUrl: './locale-view-card.component.html',
    styleUrls: ['./locale-view-card.component.css']
})
export class LocaleViewCardComponent implements OnInit {

    @Output() save = new EventEmitter<TranslationLocale>();

    public readonly form: FormGroup;
    public loading: boolean = false;

    private _locale: TranslationLocale;

    constructor(private formBuilder: FormBuilder,
                private translationLocaleService: TranslationLocaleService,
                private notificationService: NotificationService) {
        this.form = this.formBuilder.group(
            {
                displayName: this.formBuilder.control('', []),
                language: this.formBuilder.control('', [Validators.required, Validators.minLength(2), Validators.maxLength(2)]),
                region: this.formBuilder.control('', [Validators.minLength(2), Validators.maxLength(2)]),
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
        let displayName = this.form.controls['displayName'].value;

        return _.isEmpty(displayName) ? null : displayName.trim();
    }

    public get language(): string {
        let language = this.form.controls['language'].value;

        return _.isEmpty(language) ? null : language.trim().toLowerCase();
    }

    public get region(): string {
        let region = this.form.controls['region'].value;

        return _.isEmpty(region) ? null : region.trim().toUpperCase();
    }

    public get variants(): string[] {
        let variants = this.form.controls['variants'].value;

        return _.isEmpty(variants) ? [] : variants.trim().split(' ');
    }

    public get icon(): string {
        return this.form.controls['icon'].value;
    }

    public get iconClass(): string {
        return `flag-icon ${this.form.controls['icon'].value}`;
    }

    public onSave() {
        this.loading = true;

        if (this.locale.id) {
            this.translationLocaleService
                .updateLocale(this.toUpdatedLocale())
                .toPromise()
                .then(translationLocale => this.locale = translationLocale)
                .then(translationLocale => this.save.emit(translationLocale))
                .catch(error => {
                    this.notificationService.displayErrorMessage('ADMIN.LOCALES.ERROR.UPDATE', error);
                })
                .finally(() => this.loading = false);
        } else {
            this.translationLocaleService
                .createLocale(this.toNewLocale())
                .toPromise()
                .then(translationLocale => this.locale = translationLocale)
                .then(translationLocale => this.save.emit(translationLocale))
                .catch(error => {
                    this.notificationService.displayErrorMessage('ADMIN.LOCALES.ERROR.SAVE', error);
                })
                .finally(() => this.loading = false);
        }
    }

    public resetForm() {
        this.form.controls['displayName'].setValue(this.locale.displayName);
        this.form.controls['language'].setValue(this.locale.language);
        this.form.controls['region'].setValue(this.locale.region);
        this.form.controls['variants'].setValue(this.locale.variants);
        this.form.controls['icon'].setValue(this.locale.icon);

        this.form.markAsPristine();
    }

    private toNewLocale() {
        return {
            language: this.language,
            displayName: this.displayName,
            region: this.region,
            variants: this.variants,
            icon: this.icon
        };
    }

    private toUpdatedLocale(): TranslationLocale {
        return new TranslationLocale(this.locale.id, this.language, this.icon, this.displayName, this.region, this.variants);
    }
}
