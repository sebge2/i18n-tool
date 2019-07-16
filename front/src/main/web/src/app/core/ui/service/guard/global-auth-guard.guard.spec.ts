import { TestBed, async, inject } from '@angular/core/testing';

import { GlobalAuthGuard } from './global-auth-guard.service';

describe('GlobalAuthGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GlobalAuthGuard]
    });
  });

  it('should ...', inject([GlobalAuthGuard], (guard: GlobalAuthGuard) => {
    expect(guard).toBeTruthy();
  }));
});
