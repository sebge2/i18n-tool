import { TestBed } from '@angular/core/testing';

import { ExternalTranslatorService } from './external-translator.service';

describe('ExternalTranslatorService', () => {
  let service: ExternalTranslatorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExternalTranslatorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
