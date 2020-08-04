import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import { ErrorNotificationComponent } from './component/error-notification/error-notification.component';
import {ErrorMessagesNotificationComponent} from "./component/validation-result-notification/error-messages-notification.component";
import {CoreSharedLibModule} from "../shared/core-shared-lib.module";
import {CoreSharedModule} from "../shared/core-shared-module";

@NgModule({
    declarations: [
        ErrorNotificationComponent,
        ErrorMessagesNotificationComponent
    ],
    imports: [
        CommonModule,
        CoreSharedLibModule,
        CoreSharedModule,
    ],
    providers: [],
    exports: [],
    entryComponents:[ErrorNotificationComponent, ErrorMessagesNotificationComponent]
})
export class CoreNotificationModule {
}
