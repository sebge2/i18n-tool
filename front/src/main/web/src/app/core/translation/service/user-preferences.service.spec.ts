import { TestBed } from '@angular/core/testing';

import { UserPreferencesService } from './user-preferences.service';

describe('UserPreferencesService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  xit('should be created', () => {
    const service: UserPreferencesService = TestBed.inject(UserPreferencesService);

    expect(service).toBeTruthy();
  });
});
