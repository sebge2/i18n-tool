import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TranslationsComponent} from "./component/translations/translations.component";
import {RouterModule, Routes} from "@angular/router";
import {WorkspaceSelectorComponent} from './component/translations-search-bar/workspace-selector/workspace-selector.component';
import {TranslationsSearchBarComponent} from './component/translations-search-bar/translations-search-bar.component';
import {TranslationCriterionSelectorComponent} from './component/translations-search-bar/translation-criterion-selector/translation-criterion-selector.component';
import {TranslationsTableComponent} from './component/translations-table/translations-table.component';
import {TranslationEditingCellComponent} from './component/translations-table/translation-editing-cell/translation-editing-cell.component';
import {TranslationsStartReviewComponent} from './component/translations-start-review/translations-start-review.component';
import {CoreSharedModule} from "../core/shared/core-shared-module";
import {CoreAuthModule} from "../core/auth/core-auth.module";
import {CoreTranslationModule} from "../core/translation/core-translation-module";

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
        TranslationsStartReviewComponent
    ],
    entryComponents: [
        TranslationsStartReviewComponent
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
