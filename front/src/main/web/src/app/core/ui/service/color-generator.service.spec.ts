import { TestBed } from '@angular/core/testing';

import { ColorGeneratorService } from './color-generator.service';

describe('ColorGeneratorService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  xit('should be created', () => {
    const service: ColorGeneratorService = TestBed.inject(ColorGeneratorService);

    expect(service).toBeTruthy();
  });
});
