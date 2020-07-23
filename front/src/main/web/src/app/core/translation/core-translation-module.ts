import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TranslationLocaleSelectorComponent} from "./component/translation-locale-selector/translation-locale-selector.component";
import {CoreSharedModule} from "../shared/core-shared-module";
import {CoreSharedLibModule} from "../shared/core-shared-lib.module";
import {WorkspaceSelectorComponent} from "./component/workspace-selector/workspace-selector.component";

@NgModule({
    declarations: [
        TranslationLocaleSelectorComponent,
        WorkspaceSelectorComponent
    ],
    imports: [
        CommonModule,
        CoreSharedLibModule,
        CoreSharedModule,
    ],
    exports: [
        TranslationLocaleSelectorComponent,
        WorkspaceSelectorComponent
    ]
})
export class CoreTranslationModule {

}
