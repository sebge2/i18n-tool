import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {TextSelection, TranslationsTableState} from '../../model/search/translation-search-state.model';
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import * as _ from "lodash";
import {ToolBarService} from "@i18n-core-shared";
import {DICTIONARY_TOOL_OPEN} from "@i18n-dictionary";
import {TranslationLocale} from "@i18n-core-translation";

export const MAX_CHARACTER = 12;

@Component({
    selector: 'app-translations-tool-bar',
    templateUrl: './translations-tool-bar.component.html',
    styleUrls: ['./translations-tool-bar.component.css'],
})
export class TranslationsToolBarComponent implements OnInit, OnDestroy {

    static LABEL_NO_TEXT = 'TRANSLATIONS.TOOL_BAR.TRANSLATE_BUTTON_LABEL';
    static LABEL_WITH_TEXT = 'TRANSLATIONS.TOOL_BAR.TRANSLATE_BUTTON_WITH_TEXT_LABEL';


    @Input() state: TranslationsTableState = new TranslationsTableState();

    translateButtonLabelKey: string = TranslationsToolBarComponent.LABEL_NO_TEXT;
    translateButtonText: string;
    selectedText: TextSelection;

    private readonly _destroyed$ = new Subject<void>();

    constructor(private _toolBarService: ToolBarService) {
    }

    ngOnInit(): void {
        this.state.textSelection
            .pipe(takeUntil(this._destroyed$))
            .subscribe(textSelection => {
                this.selectedText = textSelection;


                if (!_.isNil(_.get(this.selectedText, 'text'))) {
                    this.translateButtonLabelKey = TranslationsToolBarComponent.LABEL_WITH_TEXT;

                    if (this.selectedText.text.length > MAX_CHARACTER) {
                        this.translateButtonText = `${this.selectedText.text.substring(0, MAX_CHARACTER)}...`;
                    } else {
                        this.translateButtonText = this.selectedText.text;
                    }
                } else {
                    this.translateButtonLabelKey = TranslationsToolBarComponent.LABEL_NO_TEXT;
                    this.translateButtonText = null;
                }
            })
    }

    ngOnDestroy(): void {
        this._destroyed$.next(null);
        this._destroyed$.complete();
    }

    onPreviousPage() {
        this.state.goOnPreviousPage();
    }

    onTranslate() {
        const text = _.get(this.selectedText, 'text');

        let fromLocale = _.get(this.selectedText, 'locale');
        if (!fromLocale) {
            fromLocale = this._guessFromTranslation();
        }

        const targetLocale = this._guessTargetTranslation(fromLocale);

        this._toolBarService.select(
            DICTIONARY_TOOL_OPEN(text, fromLocale, targetLocale)
        );
    }

    onNextPage() {
        this.state.goOnNextPage();
    }

    private _guessFromTranslation(): TranslationLocale | undefined {
        return _.chain(_.get(this.state.newSearchRequestSync, 'locales'))
            .first()
            .value();
    }

    private _guessTargetTranslation(fromLocale?: TranslationLocale): TranslationLocale | undefined {
        return _.chain(_.get(this.state.newSearchRequestSync, 'locales'))
            .filter(locale =>
                !_.eq(
                    _.get(locale, 'id'),
                    _.get(fromLocale, 'id')
                )
            )
            .first()
            .value();
    }
}
