import {inject, TestBed} from '@angular/core/testing';

import {LogoutGuard} from './logout.guard';
import {HttpClientModule} from "@angular/common/http";
import {RouterModule} from "@angular/router";
import {CoreUiModule} from "../../../ui/core-ui.module";

describe('LogoutGuard', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientModule,
                RouterModule.forRoot([]),
                CoreUiModule
            ],
            providers: [LogoutGuard]
        });
    });

    it('should ...', inject([LogoutGuard], (guard: LogoutGuard) => {
        expect(guard).toBeTruthy();
    }));
});
