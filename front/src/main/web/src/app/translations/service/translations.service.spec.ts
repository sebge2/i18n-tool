import {getTestBed, TestBed} from '@angular/core/testing';

import {TranslationsService} from './translations.service';
import {NotificationService} from "../../core/notification/service/notification.service";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {CoreSharedModule} from "../../core/shared/core-shared-module";

describe('TranslationsService', () => {
    let injector: TestBed;
    let service: TranslationsService;
    let notificationService: NotificationService;

    beforeEach(() => {
        notificationService = jasmine.createSpyObj('notificationService', ['displayErrorMessage']);

        TestBed
            .configureTestingModule({
                imports: [
                    HttpClientTestingModule,
                    CoreSharedModule
                ]
            });

        injector = getTestBed();
        service = injector.get(TranslationsService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
