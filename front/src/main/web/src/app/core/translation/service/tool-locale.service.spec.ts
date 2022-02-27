import { getTestBed, TestBed } from '@angular/core/testing';

import { ToolLocaleService } from './tool-locale.service';
import { TranslateService } from '@ngx-translate/core';
import { UserPreferencesService } from './user-preferences.service';
import { ActivatedRoute } from '@angular/router';

describe('ToolLocaleService', () => {
  let injector: TestBed;
  let service: ToolLocaleService;
  let translateService: TranslateService;
  let preferencesService: UserPreferencesService;
  let route: ActivatedRoute;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: TranslateService, useValue: translateService },
        { provide: UserPreferencesService, useValue: preferencesService },
        { provide: ActivatedRoute, useValue: route },
      ],
    });

    injector = getTestBed();
    service = injector.get(ToolLocaleService);
  });

  // TODO issue-125
  xit('should be created', () => {
    expect(service).toBeTruthy();
  });
});
