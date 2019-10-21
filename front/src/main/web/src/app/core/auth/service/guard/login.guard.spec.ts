import {inject, TestBed} from '@angular/core/testing';

import {LoginGuard} from './login.guard';
import {HttpClientModule} from "@angular/common/http";
import {RouterModule} from "@angular/router";
import {CoreUiModule} from "../../../ui/core-ui.module";

describe('LoginGuard', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientModule,
                RouterModule.forRoot([]),
                CoreUiModule
            ],
            providers: [LoginGuard]
        });
    });

    it('should ...', inject([LoginGuard], (guard: LoginGuard) => {
        expect(guard).toBeTruthy();
    }));
});
