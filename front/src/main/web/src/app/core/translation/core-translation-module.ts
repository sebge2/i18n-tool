import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TranslationLocaleSelectorComponent} from "./component/translation-locale-selector/translation-locale-selector.component";
import {CoreSharedModule} from "../shared/core-shared-module";
import {CoreSharedLibModule} from "../shared/core-shared-lib.module";

@NgModule({
    declarations: [
        TranslationLocaleSelectorComponent
    ],
    imports: [
        CommonModule,
        CoreSharedLibModule,
        CoreSharedModule,
    ],
    exports: [
        TranslationLocaleSelectorComponent
    ]
})
export class CoreTranslationModule {

}
