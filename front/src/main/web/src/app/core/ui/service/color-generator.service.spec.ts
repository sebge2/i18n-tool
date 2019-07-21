import { TestBed } from '@angular/core/testing';

import { ColorGeneratorService } from './color-generator.service';

describe('ColorGeneratorService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ColorGeneratorService = TestBed.get(ColorGeneratorService);
    expect(service).toBeTruthy();
  });
});
