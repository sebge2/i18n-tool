import { TestBed } from '@angular/core/testing';

import { UserSessionService } from './user-session.service';
import {HttpClientModule} from "@angular/common/http";
import {CoreEventModule} from "../../event/core-event.module";

describe('UserSessionService', () => {
  beforeEach(() => TestBed.configureTestingModule({imports: [HttpClientModule, CoreEventModule]}));

  it('should be created', () => {
    const service: UserSessionService = TestBed.get(UserSessionService);
    expect(service).toBeTruthy();
  });
});
