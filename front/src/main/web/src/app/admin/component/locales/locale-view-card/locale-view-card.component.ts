import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TranslationLocale} from "../../../../translations/model/translation-locale.model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {TranslationLocaleService} from "../../../../translations/service/translation-locale.service";
import {NotificationService} from "../../../../core/notification/service/notification.service";
import * as _ from "lodash";
import {TranslationLocaleCreationDto} from "../../../../api";
import {Locale} from "../../../../core/translation/model/locale.model";
import {startWith} from "rxjs/operators";
import {getStringValue} from "../../../../core/shared/utils/form-utils";

@Component({
    selector: 'app-locale-view-card',
    templateUrl: './locale-view-card.component.html',
    styleUrls: ['./locale-view-card.component.css']
})
export class LocaleViewCardComponent implements OnInit {

    @Output() save = new EventEmitter<TranslationLocale>();
    @Output() delete = new EventEmitter<TranslationLocale>();

    public readonly form: FormGroup;
    public loading: boolean = false;

    private _title: string;
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
        this.form.valueChanges.pipe(startWith(<Object>null)).subscribe(_ => this.resetTitle());
    }

    @Input()
    public get locale() {
        return this._locale;
    }

    public set locale(value: TranslationLocale) {
        this._locale = value;

        this.resetForm();
    }

    public get title(): string {
        return this._title;
    }

    public get displayName(): string {
        return getStringValue(this.form.controls['displayName']);
    }

    public get language(): string {
        let language = getStringValue(this.form.controls['language']);

        return _.isEmpty(language) ? null : language.toLowerCase();
    }

    public get region(): string {
        let region = getStringValue(this.form.controls['region']);

        return _.isEmpty(region) ? null : region.toUpperCase();
    }

    public get variants(): string[] {
        let variants = getStringValue(this.form.controls['variants']);

        return _.isEmpty(variants) ? [] : variants.split(' ');
    }

    public get icon(): string {
        return getStringValue(this.form.controls['icon']);
    }

    public get iconClass(): string {
        return `flag-icon ${this.form.controls['icon'].value}`;
    }

    public resetForm() {
        this.form.controls['displayName'].setValue(this.locale.displayName);
        this.form.controls['language'].setValue(this.locale.language);
        this.form.controls['region'].setValue(this.locale.region);
        this.form.controls['variants'].setValue(!_.isEmpty(this.locale.variants) ? _.join(this.locale.variants, ' ') : '');
        this.form.controls['icon'].setValue(this.locale.icon);

        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

    public onSave() {
        this.loading = true;

        if (this.locale.id) {
            this.translationLocaleService
                .updateLocale(this.toUpdatedLocale())
                .toPromise()
                .then(translationLocale => this.locale = translationLocale)
                .then(translationLocale => this.save.emit(translationLocale))
                .catch(error => this.notificationService.displayErrorMessage('ADMIN.LOCALES.ERROR.UPDATE', error))
                .finally(() => this.loading = false);
        } else {
            this.translationLocaleService
                .createLocale(this.toNewLocale())
                .toPromise()
                .then(translationLocale => this.locale = translationLocale)
                .then(translationLocale => this.save.emit(translationLocale))
                .catch(error => this.notificationService.displayErrorMessage('ADMIN.LOCALES.ERROR.SAVE', error))
                .finally(() => this.loading = false);
        }
    }

    public onDelete() {
        if (this.locale.id) {
            this.loading = true;
            this.translationLocaleService
                .deleteLocale(this.locale.id)
                .toPromise()
                .then(translationLocale => this.locale = translationLocale)
                .then(translationLocale => this.save.emit(translationLocale))
                .catch(error => this.notificationService.displayErrorMessage('ADMIN.LOCALES.ERROR.DELETE', error))
                .finally(() => this.loading = false);
        } else {
            this.delete.emit();
        }
    }

    private resetTitle() {
        this._title = !_.isEmpty(this.displayName)
            ? this.displayName
            : this.toLocale().toString();
    }

    private toNewLocale(): TranslationLocaleCreationDto {
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

    private toLocale(): Locale {
        return new Locale(this.language, this.region, this.variants)
    }
}
