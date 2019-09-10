import { TestBed } from '@angular/core/testing';

import { EventService } from './event.service';
import {CoreEventModule} from "../core-event.module";

describe('EventService', () => {
  beforeEach(() => TestBed.configureTestingModule({imports: [CoreEventModule]}));

  it('should be created', () => {
    const service: EventService = TestBed.get(EventService);
    expect(service).toBeTruthy();
  });
});
