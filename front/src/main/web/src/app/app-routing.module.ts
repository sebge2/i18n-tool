import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [
    {path: '', pathMatch: 'full', redirectTo: '/translations'},
    {path: '', loadChildren: () => import('./core/ui/core-ui.module').then(m => m.CoreUiModule)},
    {path: 'error', loadChildren: () => import('./error/core-error.module').then(m => m.CoreErrorModule)},
    {path: 'logout', loadChildren: () => import('./core/auth/core-auth.module').then(m => m.CoreAuthModule)},
    {path: '**', redirectTo: "/error/404"}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
