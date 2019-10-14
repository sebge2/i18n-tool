import {getTestBed, TestBed} from '@angular/core/testing';

import {EventService} from './event.service';
import {CoreEventModule} from "../core-event.module";
import {NotificationService} from "../../notification/service/notification.service";

describe('EventService', () => {
    let injector: TestBed;
    let service: EventService;
    let notificationService: NotificationService;

    beforeEach(() => {
        notificationService = jasmine.createSpyObj('notificationService', ['displayErrorMessage']);

        TestBed.configureTestingModule({
            imports: [CoreEventModule]
        });

        injector = getTestBed();
        service = injector.get(EventService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
