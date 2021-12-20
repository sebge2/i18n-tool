import { TestBed } from '@angular/core/testing';

import { TranslationLocaleService } from './translation-locale.service';

describe('TranslationLocaleService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  xit('should be created', () => {
    const service: TranslationLocaleService = TestBed.inject(TranslationLocaleService);
    expect(service).toBeTruthy();
  });
});
