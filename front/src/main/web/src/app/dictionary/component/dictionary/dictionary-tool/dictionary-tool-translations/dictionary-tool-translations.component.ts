import {Component, Input} from '@angular/core';
import {TextTranslation, TextTranslations} from "@i18n-core-translation";
import * as _ from "lodash";
import {NotificationService} from "@i18n-core-notification";
import {ButtonSize, TranslationKey} from "@i18n-core-shared";

@Component({
    selector: 'app-dictionary-tool-translations',
    templateUrl: './dictionary-tool-translations.component.html',
    styleUrls: ['./dictionary-tool-translations.component.scss']
})
export class DictionaryToolTranslationsComponent {

    hasPrevious: boolean = false;
    hasNext: boolean = false;

    ButtonSize: typeof ButtonSize = ButtonSize;

    private _selectedText: string;
    private _translations: TextTranslations = new TextTranslations();
    private _currentTranslationIndex: number;

    constructor(private _notificationService: NotificationService) {
    }

    @Input()
    get translations(): TextTranslations {
        return this._translations;
    }

    set translations(value: TextTranslations) {
        this._translations = value;
        this._updateFields(value);
    }

    get currentTranslation(): TextTranslation | undefined {
        return (this._currentTranslationIndex >= 0)
            ? this._translations.translations[this._currentTranslationIndex]
            : null;
    }

    get textToCopy(): string {
        return _.isEmpty(this._selectedText)
            ? _.get(this.currentTranslation, 'text')
            : this._selectedText;
    }

    onGoPrevious() {
        this._currentTranslationIndex--;
        this._updatePreviousNextFlags();
    }

    onGoNext() {
        this._currentTranslationIndex++;
        this._updatePreviousNextFlags();
    }

    onTextCopied() {
        this._notificationService.displayInfoMessage(new TranslationKey('DICTIONARY.TABLE.TOOL_BAR.TEXT_COPIED'));
    }

    toExternalLink(translation: TextTranslation): string {
        return `<a href=\"${translation.externalSource.url}\" target=\"_blank\">${translation.externalSource.label}</a>`;
    }

    onSelectedText(selectedText: string): void {
        this._selectedText = selectedText;
    }

    private _updateFields(value: TextTranslations): void {
        this._currentTranslationIndex = _.some(value.translations)
            ? 0
            : -1;

        this._updatePreviousNextFlags();
    }

    private _updatePreviousNextFlags() {
        this.hasPrevious = this._currentTranslationIndex > 0;
        this.hasNext = this._currentTranslationIndex < (_.size(this.translations.translations) - 1);
    }
}
