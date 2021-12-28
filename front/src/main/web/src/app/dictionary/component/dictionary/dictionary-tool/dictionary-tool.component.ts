import {Component, Input, OnDestroy} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
    TextTranslations,
    TranslationLocale,
    TranslationLocaleService,
    TranslationService
} from "@i18n-core-translation";
import * as _ from "lodash";
import {auditTime, catchError, filter, mergeMap, takeUntil, tap} from "rxjs/operators";
import {Observable, of, Subject} from "rxjs";
import {NotificationService} from "@i18n-core-notification";

export const REQUESTED_FROM_LOCALE_INPUT_NAME = 'fromLocaleId';
export const REQUESTED_TARGET_LOCALE_INPUT_NAME = 'targetLocaleId';
export const REQUESTED_TEXT_INPUT_NAME = 'text';

@Component({
    selector: 'app-dictionary-tool',
    templateUrl: './dictionary-tool.component.html',
    styleUrls: ['./dictionary-tool.component.scss'],
})
export class DictionaryToolComponent implements OnDestroy {

    readonly form: FormGroup;

    translations: TextTranslations;
    searching: boolean = false;

    private _availableLocales: TranslationLocale[] = [];
    private readonly _destroyed$ = new Subject<void>();

    constructor(
        private _formBuilder: FormBuilder,
        private _translationService: TranslationService,
        private _localeService: TranslationLocaleService,
        private _notificationService: NotificationService,
    ) {
        this.form = this._formBuilder.group({
            fromLocale: this._formBuilder.control(null, [Validators.required]),
            text: this._formBuilder.control(null, [Validators.required]),
            targetLocale: this._formBuilder.control(null, [Validators.required]),
        });

        this._localeService
            .getAvailableLocales()
            .pipe(takeUntil(this._destroyed$))
            .subscribe(locales => this._availableLocales = locales);

        this.form
            .valueChanges
            .pipe(takeUntil(this._destroyed$))
            .subscribe(() => this._resetTranslations());

        this.form
            .valueChanges
            .pipe(
                takeUntil(this._destroyed$),
                filter(() => this.form.valid),
                tap(() => this.searching = true),

                auditTime(2000),

                mergeMap(() => this.search()),

                catchError((error) => {
                    console.error('Error while translating.', error);
                    this._notificationService.displayErrorMessage('DICTIONARY.TABLE.TOOL_BAR.ERROR.TRANSLATE', error);

                    return of(new TextTranslations());
                }),
            )
            .subscribe(translations => {
                this.translations = translations;
                this.searching = false;
            });
    }

    ngOnDestroy(): void {
        this._destroyed$.next(null);
        this._destroyed$.complete();
    }

    @Input()
    get fromLocaleId(): string {
        return _.get(this.fromLocale, 'id');
    }

    set fromLocaleId(value: string) {
        this.fromLocale = _.find(this._availableLocales, locale => _.eq(value, locale.id));
    }

    get fromLocale(): TranslationLocale | undefined {
        return this.form.controls['fromLocale'].value;
    }

    set fromLocale(value: TranslationLocale) {
        this.form.controls['fromLocale'].setValue(value);
    }

    @Input()
    get text(): string {
        return this.form.controls['text'].value;
    }

    set text(value: string) {
        this.form.controls['text'].setValue(value);
    }

    @Input()
    get targetLocaleId(): string | undefined {
        return _.get(this.targetLocale, 'id');
    }

    set targetLocaleId(value: string) {
        this.targetLocale = _.find(this._availableLocales, locale => _.eq(value, locale.id));
    }

    get targetLocale(): TranslationLocale | undefined {
        return this.form.controls['targetLocale'].value;
    }

    set targetLocale(value: TranslationLocale) {
        this.form.controls['targetLocale'].setValue(value);
    }

    onInverse(): void {
        const targetLocale = this.targetLocale;
        const fromLocale = this.fromLocale;

        this.targetLocaleId = _.get(fromLocale, 'id');
        this.fromLocaleId = _.get(targetLocale, 'id');
    }

    search(): Observable<TextTranslations> {
        return this._translationService
            .translateText(
                this.text,
                this.fromLocaleId,
                this.targetLocaleId
            );
    }

    private _resetTranslations(): void {
        this.translations = null;
    }
}
