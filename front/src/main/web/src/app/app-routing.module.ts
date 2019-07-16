import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [
    {path: '', pathMatch: 'full', redirectTo: '/translations'},
    {path: '', loadChildren: './core/ui/core-ui.module#CoreUiModule'},
    {path: 'error', loadChildren: './error/core-error.module#CoreErrorModule'},
    {path: '**', redirectTo: "/error/404"}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
