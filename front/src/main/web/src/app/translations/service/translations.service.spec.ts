import { TestBed } from '@angular/core/testing';

import { TranslationsService } from './translations.service';
import {HttpClientModule} from "@angular/common/http";

describe('TranslationsService', () => {
  beforeEach(() => TestBed.configureTestingModule({imports: [HttpClientModule]}));

  it('should be created', () => {
    const service: TranslationsService = TestBed.get(TranslationsService);
    expect(service).toBeTruthy();
  });
});
