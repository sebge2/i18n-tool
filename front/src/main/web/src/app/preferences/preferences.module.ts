import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {PreferencesComponent} from './component/preferences/preferences.component';
import {RouterModule, Routes} from "@angular/router";

const appRoutes: Routes = [
    {path: '', pathMatch: 'full', component: PreferencesComponent}
];

@NgModule({
    declarations: [PreferencesComponent],
    imports: [
        CommonModule,
        RouterModule.forChild(appRoutes)
    ],
    exports: [RouterModule]
})
export class PreferencesModule {
}
