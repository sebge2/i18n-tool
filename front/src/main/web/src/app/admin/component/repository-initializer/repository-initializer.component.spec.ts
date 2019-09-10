import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {RepositoryInitializerComponent} from './repository-initializer.component';
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {HttpClientModule} from "@angular/common/http";
import {CoreEventModule} from "../../../core/event/core-event.module";
import {TranslateModule} from "@ngx-translate/core";
import {CoreUiModule} from "../../../core/ui/core-ui.module";

describe('RepositoryInitializerComponent', () => {
    let component: RepositoryInitializerComponent;
    let fixture: ComponentFixture<RepositoryInitializerComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                CoreSharedModule,
                CoreUiModule,
                CoreEventModule,
                HttpClientModule,
                TranslateModule.forRoot()
            ],
            declarations: [RepositoryInitializerComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(RepositoryInitializerComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
