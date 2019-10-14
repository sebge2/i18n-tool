import {getTestBed, TestBed} from '@angular/core/testing';

import {NotificationService} from './notification.service';
import {CoreNotificationModule} from "../core-notification.module";
import {CoreUiModule} from "../../ui/core-ui.module";

describe('NotificationService', () => {
    let injector: TestBed;
    let service: NotificationService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [CoreNotificationModule, CoreUiModule]
        });

        injector = getTestBed();
        service = injector.get(NotificationService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
