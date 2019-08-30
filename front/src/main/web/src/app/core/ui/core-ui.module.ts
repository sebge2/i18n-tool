import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MainComponent} from './component/main/main.component';
import {RouterModule, Routes} from "@angular/router";
import {GlobalAuthGuard} from "./service/guard/global-auth-guard.service";
import {MaterialModule} from './material.module';
import {MenuComponent} from "./component/menu/menu.component";
import {HeaderComponent} from "./component/header/header.component";

const appRoutes: Routes = [
    {
        path: '',
        component: MainComponent,
        canActivate: [GlobalAuthGuard],
        children: [
            {
                path: 'translations',
                loadChildren: () => import('./../../translations/translations.module').then(m => m.TranslationsModule)
            },
            {
                path: 'settings',
                loadChildren: () => import('./../../settings/settings.module').then(m => m.SettingsModule)
            },
            {
                path: 'admin',
                loadChildren: () => import('./../../admin/admin.module').then(m => m.AdminModule)
            }
        ]
    }
];

@NgModule({
    declarations: [
        MainComponent,
        MenuComponent,
        HeaderComponent
    ],
    imports: [
        CommonModule,
        RouterModule.forChild(appRoutes),
        MaterialModule
    ],
    exports: [
        MaterialModule
    ]
})
export class CoreUiModule {

}
