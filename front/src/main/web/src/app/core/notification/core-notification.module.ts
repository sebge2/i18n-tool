import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import { ErrorNotificationComponent } from './component/error-notification/error-notification.component';

@NgModule({
    declarations: [ErrorNotificationComponent],
    imports: [
        CommonModule
    ],
    providers: [],
    exports: [],
    entryComponents:[ErrorNotificationComponent]
})
export class CoreNotificationModule {
}
