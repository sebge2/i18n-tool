import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SettingsComponent} from './component/settings/settings.component';
import {RouterModule, Routes} from "@angular/router";

const appRoutes: Routes = [
    {path: '', pathMatch: 'full', component: SettingsComponent}
];

@NgModule({
    declarations: [SettingsComponent],
    imports: [
        CommonModule,
        RouterModule.forChild(appRoutes)
    ],
    exports: [RouterModule]
})
export class SettingsModule {
}
