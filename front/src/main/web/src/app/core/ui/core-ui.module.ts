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
            {path: 'translations', loadChildren: () => import('./../../translations/translations.module').then(m => m.TranslationsModule)},
            {path: 'settings', loadChildren: () => import('./../../settings/settings.module').then(m => m.SettingsModule)}
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
