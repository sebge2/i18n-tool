import {getTestBed, TestBed} from '@angular/core/testing';

import {NotificationService} from './notification.service';
import {CoreSharedModule} from "../../shared/core-shared-module";

describe('NotificationService', () => {
    let injector: TestBed;
    let service: NotificationService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                CoreSharedModule
            ]
        });

        injector = getTestBed();
        service = injector.get(NotificationService);
    });

    xit('should be created', () => {
        expect(service).toBeTruthy();
    });
});
