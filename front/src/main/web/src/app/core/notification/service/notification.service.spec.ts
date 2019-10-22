import {getTestBed, TestBed} from '@angular/core/testing';

import {NotificationService} from './notification.service';
import {CoreUiModule} from "../../ui/core-ui.module";

describe('NotificationService', () => {
    let injector: TestBed;
    let service: NotificationService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                CoreUiModule
            ]
        });

        injector = getTestBed();
        service = injector.get(NotificationService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
