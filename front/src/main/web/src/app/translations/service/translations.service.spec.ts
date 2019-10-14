import {getTestBed, TestBed} from '@angular/core/testing';

import {TranslationsService} from './translations.service';
import {HttpClientModule} from "@angular/common/http";
import {NotificationService} from "../../core/notification/service/notification.service";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {CoreEventModule} from "../../core/event/core-event.module";
import {CoreUiModule} from "../../core/ui/core-ui.module";

describe('TranslationsService', () => {
    let injector: TestBed;
    let service: TranslationsService;
    let notificationService: NotificationService;

    beforeEach(() => {
        notificationService = jasmine.createSpyObj('notificationService', ['displayErrorMessage']);

        TestBed
            .configureTestingModule({
              imports: [HttpClientTestingModule, CoreUiModule],
            });

        injector = getTestBed();
        service = injector.get(TranslationsService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
