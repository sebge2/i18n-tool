import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {GlobalAuthGuard} from "./core/ui/service/guard/global-auth-guard.service";

const routes: Routes = [
    {path: '', pathMatch: 'full', redirectTo: '/translations', canActivate: [GlobalAuthGuard]},
    {path: '', loadChildren: './core/ui/core-ui.module#CoreUiModule'},
    {path: 'error', loadChildren: './core/error/core-error.module#CoreErrorModule'},
    {path: '**', redirectTo: "/error/404"}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
