import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {MainMessageComponent} from './component/main-message/main-message.component';

@NgModule({
  declarations: [
    MainMessageComponent
  ],
  imports: [
    CommonModule
  ],
  exports: [
    CommonModule,

    MainMessageComponent
  ]
})
export class CoreSharedModule {

}
