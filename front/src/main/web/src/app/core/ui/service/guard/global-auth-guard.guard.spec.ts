import {inject, TestBed} from '@angular/core/testing';

import {GlobalAuthGuard} from './global-auth-guard.service';
import {RouterModule} from "@angular/router";
import {HttpClientModule} from "@angular/common/http";
import {CoreUiModule} from "../../core-ui.module";

describe('GlobalAuthGuard', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientModule, RouterModule.forRoot([]), CoreUiModule],
            providers: [GlobalAuthGuard]
        });
    });

    it('should ...', inject([GlobalAuthGuard], (guard: GlobalAuthGuard) => {
        expect(guard).toBeTruthy();
    }));
});
