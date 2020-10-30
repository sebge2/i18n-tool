import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TranslationsComponent} from "./component/translations/translations.component";
import {RouterModule, Routes} from "@angular/router";
import {TranslationsSearchBarComponent} from './component/translations-search-bar/translations-search-bar.component';
import {TranslationCriterionSelectorComponent} from './component/translations-search-bar/translation-criterion-selector/translation-criterion-selector.component';
import {TranslationsTableComponent} from './component/translations-table/translations-table.component';
import {TranslationEditingCellComponent} from './component/translations-table/translation-editing-cell/translation-editing-cell.component';
import {CoreSharedModule} from "../core/shared/core-shared-module";
import {CoreAuthModule} from "../core/auth/core-auth.module";
import {CoreTranslationModule} from "../core/translation/core-translation-module";
import {TranslationsWorkspaceRowComponent} from './component/translations-table/translations-workspace-row/translations-workspace-row.component';
import {TranslationsBundleFileRowComponent} from './component/translations-table/translations-bundle-file-row/translations-bundle-file-row.component';
import { TranslationsToolBarComponent } from './component/translations-tool-bar/translations-tool-bar.component';

const appRoutes: Routes = [
    {
        path: '', component: TranslationsComponent
    }
];

@NgModule({
    declarations: [
        TranslationsComponent,
        TranslationsSearchBarComponent,
        TranslationCriterionSelectorComponent,
        TranslationsTableComponent,
        TranslationEditingCellComponent,
        TranslationsWorkspaceRowComponent,
        TranslationsBundleFileRowComponent,
        TranslationsToolBarComponent
    ],
    imports: [
        CommonModule,
        CoreSharedModule,
        CoreAuthModule,
        CoreTranslationModule,

        RouterModule.forChild(appRoutes)
    ],
    exports: [RouterModule]
})
export class TranslationsModule {

}
