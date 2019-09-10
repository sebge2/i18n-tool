import {TestBed} from '@angular/core/testing';

import {WorkspaceService} from './workspace.service';
import {HttpClientModule} from "@angular/common/http";
import {CoreEventModule} from "../../core/event/core-event.module";

describe('WorkspaceService', () => {
    beforeEach(() => TestBed.configureTestingModule({imports: [HttpClientModule, CoreEventModule]}));

    it('should be created', () => {
        const service: WorkspaceService = TestBed.get(WorkspaceService);
        expect(service).toBeTruthy();
    });
});
