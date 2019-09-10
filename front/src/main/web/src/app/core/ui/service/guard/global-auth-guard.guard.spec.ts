import {inject, TestBed} from '@angular/core/testing';

import {GlobalAuthGuard} from './global-auth-guard.service';
import {RouterModule} from "@angular/router";
import {HttpClientModule} from "@angular/common/http";

describe('GlobalAuthGuard', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientModule, RouterModule.forRoot([])],
            providers: [GlobalAuthGuard]
        });
    });

    it('should ...', inject([GlobalAuthGuard], (guard: GlobalAuthGuard) => {
        expect(guard).toBeTruthy();
    }));
});
