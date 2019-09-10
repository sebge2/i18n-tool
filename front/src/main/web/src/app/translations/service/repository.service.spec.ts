import { TestBed } from '@angular/core/testing';

import { RepositoryService } from './repository.service';
import {HttpClientModule} from "@angular/common/http";
import {CoreEventModule} from "../../core/event/core-event.module";

describe('RepositoryService', () => {
  beforeEach(() => TestBed.configureTestingModule({imports: [HttpClientModule, CoreEventModule]}));

  it('should be created', () => {
    const service: RepositoryService = TestBed.get(RepositoryService);
    expect(service).toBeTruthy();
  });
});
