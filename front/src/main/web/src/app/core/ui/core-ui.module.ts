import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MainComponent} from './component/main/main.component';
import {RouterModule, Routes} from "@angular/router";
import {GlobalAuthGuard} from "./service/guard/global-auth-guard.service";

const appRoutes: Routes = [
    {
        path: '',
        component: MainComponent,
        canActivate: [GlobalAuthGuard],
        children: [
            {path: 'translations', loadChildren: './../../translations/translations.module#TranslationsModule'},
            {path: 'settings', loadChildren: './../../settings/settings.module#SettingsModule'}
        ]
    }
];

@NgModule({
    declarations: [
        MainComponent
    ],
    imports: [
        CommonModule,
        RouterModule.forChild(appRoutes)
    ],
    exports: []
})
export class CoreUiModule {

}
