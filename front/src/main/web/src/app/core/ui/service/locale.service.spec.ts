import {getTestBed, TestBed} from '@angular/core/testing';

import {LocaleService} from './locale.service';
import {TranslateService} from "@ngx-translate/core";
import {UserSettingsService} from "../../../settings/service/user-settings.service";
import {ActivatedRoute} from "@angular/router";

describe('LocaleService', () => {
    let injector: TestBed;
    let service: LocaleService;
    let translateService: TranslateService;
    let settingsService: UserSettingsService;
    let route: ActivatedRoute;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                {provide: TranslateService, useValue: translateService},
                {provide: UserSettingsService, useValue: settingsService},
                {provide: ActivatedRoute, useValue: route}
            ]
        })

        injector = getTestBed();
        service = injector.get(LocaleService);
    });

    // TODO
    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
