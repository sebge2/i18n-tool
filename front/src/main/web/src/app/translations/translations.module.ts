import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TranslationsComponent} from "./component/translations/translations.component";
import {RouterModule, Routes} from "@angular/router";

const appRoutes: Routes = [
    {
        path: '',  component: TranslationsComponent
    }
];

@NgModule({
    declarations: [
        TranslationsComponent
    ],
    imports: [
        CommonModule,
        RouterModule.forChild(appRoutes)
    ],
    exports: [RouterModule]
})
export class TranslationsModule {

}
