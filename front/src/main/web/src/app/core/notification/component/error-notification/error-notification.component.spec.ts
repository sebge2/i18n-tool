import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ErrorNotificationComponent} from './error-notification.component';
import {NotificationService} from "../../service/notification.service";
import {CoreUiModule} from "../../../ui/core-ui.module";
import {MAT_SNACK_BAR_DATA} from "@angular/material";

describe('ErrorNotificationComponent', () => {
    let component: ErrorNotificationComponent;
    let fixture: ComponentFixture<ErrorNotificationComponent>;
    let notificationService: NotificationService
    let errorMessageData: { message: string };

    beforeEach(async(() => {
        notificationService = jasmine.createSpyObj('notificationService', ['displayErrorMessage']);
        errorMessageData = {message: null};

        TestBed
            .configureTestingModule({
                declarations: [ErrorNotificationComponent],
                imports: [CoreUiModule],
                providers: [
                    {
                        provide: MAT_SNACK_BAR_DATA,
                        useValue: errorMessageData
                    },
                    {provide: NotificationService, useValue: notificationService}
                ]
            })
            .compileComponents();

        fixture = TestBed.createComponent(ErrorNotificationComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
