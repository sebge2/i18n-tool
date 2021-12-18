import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ErrorMessagesNotificationComponent } from './component/validation-result-notification/error-messages-notification.component';
import { CoreSharedLibModule } from '../shared/core-shared-lib.module';
import { CoreSharedModule } from '@i18n-core-shared';
import { NotificationSnackbarComponent } from './component/notification-snackbar/notification-snackbar.component';

@NgModule({
  declarations: [NotificationSnackbarComponent, ErrorMessagesNotificationComponent],
  imports: [CommonModule, CoreSharedLibModule, CoreSharedModule],
  providers: [],
  exports: [],
  entryComponents: [NotificationSnackbarComponent, ErrorMessagesNotificationComponent],
})
export class CoreNotificationModule {}
