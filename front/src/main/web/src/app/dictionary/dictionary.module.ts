import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from '@angular/router';
import {CoreSharedModule, TOOL_DESCRIPTOR_TOKEN, ToolDescriptor, ToolSelectionRequest} from '@i18n-core-shared';
import {CoreAuthModule} from '@i18n-core-auth';
import {CoreTranslationModule, TranslationLocale} from '@i18n-core-translation';
import {DictionaryComponent} from './component/dictionary/dictionary.component';
import {DictionaryEntryActionComponent} from './component/dictionary/dictionary-table/dictionary-entry-action/dictionary-entry-action.component';
import {DictionaryEntryEditingCellComponent} from './component/dictionary/dictionary-table/dictionary-entry-editing-cell/dictionary-entry-editing-cell.component';
import {DictionaryTableComponent} from './component/dictionary/dictionary-table/dictionary-table.component';
import {DictionaryBottomBarComponent} from './component/dictionary/dictionary-bottom-bar/dictionary-bottom-bar.component';
import {DictionaryNewEntryActionComponent} from './component/dictionary/dictionary-table/dictionary-new-entry-action/dictionary-new-entry-action.component';
import {DictionaryUploadDialogComponent} from './component/dictionary/dictionary-table/dictionary-upload-dialog/dictionary-upload-dialog.component';
import {DictionarySettingsDialogComponent} from './component/dictionary/dictionary-table/dictionary-settings-dialog/dictionary-settings-dialog.component';
import {
    DictionaryToolComponent,
    REQUESTED_FROM_LOCALE_INPUT_NAME, REQUESTED_TARGET_LOCALE_INPUT_NAME, REQUESTED_TEXT_INPUT_NAME
} from './component/dictionary/dictionary-tool/dictionary-tool.component';
import * as _ from "lodash";
import { DictionaryToolTranslationsComponent } from './component/dictionary/dictionary-tool/dictionary-tool-translations/dictionary-tool-translations.component';

export const DICTIONARY_TOOL_ID = 'dictionary';

export const DICTIONARY_TOOL_OPEN = (text?: string, fromLocale?: TranslationLocale, targetLocale?: TranslationLocale) => new ToolSelectionRequest(
    DICTIONARY_TOOL_ID,
    {
        [REQUESTED_TEXT_INPUT_NAME]: text,
        [REQUESTED_FROM_LOCALE_INPUT_NAME]: _.get(fromLocale, ['id']),
        [REQUESTED_TARGET_LOCALE_INPUT_NAME]: _.get(targetLocale, [ 'id']),
    }
)

export const TOOL_BAR_DESCRIPTOR_PROVIDER = {
    provide: TOOL_DESCRIPTOR_TOKEN,
    useValue: new ToolDescriptor(
        DICTIONARY_TOOL_ID,
        0,
        'auto_stories',
        'DICTIONARY.TABLE.TOOL_BAR.TITLE',
        'DICTIONARY.TABLE.TOOL_BAR.DESCRIPTION',
        DictionaryToolComponent,
    ),
    multi: true
};

const appRoutes: Routes = [
    {
        path: '',
        component: DictionaryComponent,
    },
];

@NgModule({
    declarations: [
        DictionaryComponent,
        DictionaryEntryActionComponent,
        DictionaryEntryEditingCellComponent,
        DictionaryTableComponent,
        DictionaryBottomBarComponent,
        DictionaryNewEntryActionComponent,
        DictionaryUploadDialogComponent,
        DictionarySettingsDialogComponent,
        DictionaryToolComponent,
        DictionaryToolTranslationsComponent,
    ],
    imports: [
        CommonModule,
        CoreSharedModule,
        CoreAuthModule,
        CoreTranslationModule,
        RouterModule.forChild(appRoutes),
    ],
    exports: [RouterModule],
})
export class DictionaryModule {
}
