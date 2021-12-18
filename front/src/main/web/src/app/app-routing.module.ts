import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainComponent } from '@i18n-core-ui';
import { GlobalAuthGuard } from '@i18n-core-ui';

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: '/translations' },
  {
    path: '',
    component: MainComponent,
    canActivate: [GlobalAuthGuard],
    children: [
      {
        path: 'translations',
        loadChildren: () => import('./translations/translations.module').then((m) => m.TranslationsModule),
      },
      {
        path: 'dictionary',
        loadChildren: () => import('./dictionary/dictionary.module').then((m) => m.DictionaryModule),
      },
      {
        path: 'account',
        loadChildren: () => import('./account/account.module').then((m) => m.AccountModule),
      },
      {
        path: 'admin',
        loadChildren: () => import('./admin/admin.module').then((m) => m.AdminModule),
      },
    ],
  },
  { path: '', loadChildren: () => import('./core/auth/core-auth.module').then((m) => m.CoreAuthModule) },
  { path: 'error', loadChildren: () => import('./error/core-error.module').then((m) => m.CoreErrorModule) },
  { path: '**', redirectTo: '/error/404' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
