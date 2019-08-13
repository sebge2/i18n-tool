import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TranslationsComponent} from "./component/translations/translations.component";
import {RouterModule, Routes} from "@angular/router";
import {MaterialModule} from "../core/ui/material.module";
import {WorkspaceSelectorComponent} from './component/translations-search-bar/workspace-selector/workspace-selector.component';
import {ReactiveFormsModule, FormsModule} from "@angular/forms";
import {WorkspaceIconPipe} from './pipe/workspace-icon.pipe';
import {WorkspaceIconCssPipe} from './pipe/workspace-icon-css.pipe';
import {TranslationsSearchBarComponent} from './component/translations-search-bar/translations-search-bar.component';
import {TranslationLocalesSelectorComponent} from "./component/translations-search-bar/translation-locales-selector/translation-locales-selector.component";
import {LocaleIconPipe} from './pipe/locale-icon.pipe';
import {TranslationCriterionSelectorComponent} from './component/translations-search-bar/translation-criterion-selector/translation-criterion-selector.component';
import {TranslationsTableComponent} from './component/translations-table/translations-table.component';
import { TranslationEditingCellComponent } from './component/translations-table/translation-editing-cell/translation-editing-cell.component';
import { TranslationsStartReviewComponent } from './component/translations-table/translations-start-review/translations-start-review.component';

const appRoutes: Routes = [
    {
        path: '', component: TranslationsComponent
    }
];

@NgModule({
    declarations: [
        TranslationsComponent,
        WorkspaceSelectorComponent,
        WorkspaceIconPipe,
        WorkspaceIconCssPipe,
        TranslationsSearchBarComponent,
        TranslationLocalesSelectorComponent,
        TranslationCriterionSelectorComponent,
        TranslationsTableComponent,
        TranslationEditingCellComponent,
        TranslationsStartReviewComponent,
        LocaleIconPipe
    ],
    entryComponents: [
        TranslationsStartReviewComponent
    ],
    imports: [
        CommonModule,
        RouterModule.forChild(appRoutes),
        MaterialModule,
        ReactiveFormsModule,
        FormsModule
    ],
    exports: [RouterModule]
})
export class TranslationsModule {

}
