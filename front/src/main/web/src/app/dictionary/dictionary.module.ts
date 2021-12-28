import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from '@angular/router';
import {CoreSharedModule} from '@i18n-core-shared';
import {CoreAuthModule} from '@i18n-core-auth';
import {CoreTranslationModule} from '@i18n-core-translation';
import {DictionaryComponent} from './component/dictionary/dictionary.component';
import {DictionaryEntryActionComponent} from './component/dictionary/dictionary-table/dictionary-entry-action/dictionary-entry-action.component';
import {DictionaryEntryEditingCellComponent} from './component/dictionary/dictionary-table/dictionary-entry-editing-cell/dictionary-entry-editing-cell.component';
import {DictionaryTableComponent} from './component/dictionary/dictionary-table/dictionary-table.component';
import {DictionaryBottomBarComponent} from './component/dictionary/dictionary-bottom-bar/dictionary-bottom-bar.component';
import {DictionaryNewEntryActionComponent} from './component/dictionary/dictionary-table/dictionary-new-entry-action/dictionary-new-entry-action.component';
import {DictionaryUploadDialogComponent} from './component/dictionary/dictionary-table/dictionary-upload-dialog/dictionary-upload-dialog.component';
import {DictionarySettingsDialogComponent} from './component/dictionary/dictionary-table/dictionary-settings-dialog/dictionary-settings-dialog.component';

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
