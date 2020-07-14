import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MainComponent} from "./core/ui/component/main/main.component";
import {GlobalAuthGuard} from "./core/ui/service/guard/global-auth-guard.service";

const routes: Routes = [
    {path: '', pathMatch: 'full', redirectTo: '/translations'},
    {
        path: '',
        component: MainComponent,
        canActivate: [GlobalAuthGuard],
        children: [
            {
                path: 'translations',
                loadChildren: () => import('./translations/translations.module').then(m => m.TranslationsModule)
            },
            {
                path: 'preferences',
                loadChildren: () => import('./preferences/preferences.module').then(m => m.PreferencesModule)
            },
            {
                path: 'admin',
                loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)
            }
        ]
    },
    {path: '', loadChildren: () => import('./core/auth/core-auth.module').then(m => m.CoreAuthModule)},
    {path: 'error', loadChildren: () => import('./error/core-error.module').then(m => m.CoreErrorModule)},
    {path: '**', redirectTo: "/error/404"}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
