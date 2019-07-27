import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TranslationsComponent} from "./component/translations/translations.component";
import {RouterModule, Routes} from "@angular/router";
import {MaterialModule} from "../core/ui/material.module";
import {WorkspaceSelectorComponent} from './component/workspace-selector/workspace-selector.component';
import {ReactiveFormsModule} from "@angular/forms";
import {WorkspaceIconPipe} from './pipe/workspace-icon.pipe';
import {WorkspaceIconCssPipe} from './pipe/workspace-icon-css.pipe';
import {TranslationsSearchBarComponent} from './component/translations-search-bar/translations-search-bar.component';
import {TranslationLocalesSelectorComponent} from "./component/translations-search-bar/translation-locales-selector/translation-locales-selector.component";
import { LocaleIconPipe } from './pipe/locale-icon.pipe';

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
        LocaleIconPipe
    ],
    imports: [
        CommonModule,
        RouterModule.forChild(appRoutes),
        MaterialModule,
        ReactiveFormsModule
    ],
    exports: [RouterModule]
})
export class TranslationsModule {

}
