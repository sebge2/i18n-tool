import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TranslationLocalesSelectorComponent} from './translation-locales-selector.component';
import {CoreSharedModule} from "../../../../core/shared/core-shared-module";
import {TranslateModule} from "@ngx-translate/core";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {CoreUiModule} from "../../../../core/ui/core-ui.module";

describe('TranslationLocalesSelectorComponent', () => {
    let component: TranslationLocalesSelectorComponent;
    let fixture: ComponentFixture<TranslationLocalesSelectorComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                BrowserAnimationsModule,
                CoreSharedModule,
                CoreUiModule,
                TranslateModule.forRoot()
            ],
            declarations: [TranslationLocalesSelectorComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(TranslationLocalesSelectorComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy(); // TODO
    });
});
