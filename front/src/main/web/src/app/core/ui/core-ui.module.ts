import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MainComponent } from './component/main/main.component';
import { RouterModule } from '@angular/router';
import { MenuComponent } from './component/menu/menu.component';
import { HeaderComponent } from './component/header/header.component';
import { CoreAuthModule } from '@i18n-core-auth';
import { CoreSharedModule } from '@i18n-core-shared';
import { MenuItemComponent } from './component/menu/menu-item/menu-item.component';

@NgModule({
  declarations: [MainComponent, MenuComponent, HeaderComponent, MenuItemComponent],
  imports: [RouterModule, CommonModule, CoreSharedModule, CoreAuthModule],
  exports: [],
})
export class CoreUiModule {}
