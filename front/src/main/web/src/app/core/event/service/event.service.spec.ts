import {getTestBed, TestBed} from '@angular/core/testing';

import {EventService} from './event.service';
import {CoreEventModule} from "../core-event.module";
import {NotificationService} from "../../notification/service/notification.service";
import {CoreSharedModule} from "../../shared/core-shared-module";

describe('EventService', () => {
    let injector: TestBed;
    let service: EventService;
    let notificationService: NotificationService;

    beforeEach(() => {
        notificationService = jasmine.createSpyObj('notificationService', ['displayErrorMessage']);

        TestBed.configureTestingModule({
            imports: [
                CoreEventModule,
                CoreSharedModule
            ]
        });

        injector = getTestBed();
        service = injector.get(EventService);
    });

    xit('should be created', () => {
        expect(service).toBeTruthy(); // TODO issue-125
    });
});
