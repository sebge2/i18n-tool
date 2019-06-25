import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TranslationLocalesSelectorComponent} from './translation-locales-selector.component';
import {CoreSharedModule} from "../../../../core/shared/core-shared-module";
import {TranslateModule} from "@ngx-translate/core";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

describe('TranslationLocalesSelectorComponent', () => {
    let component: TranslationLocalesSelectorComponent;
    let fixture: ComponentFixture<TranslationLocalesSelectorComponent>;

    beforeEach(async(() => {
        TestBed
            .configureTestingModule({
                imports: [
                    BrowserAnimationsModule,
                    CoreSharedModule,
                    TranslateModule.forRoot()
                ],
                declarations: [TranslationLocalesSelectorComponent]
            })
            .compileComponents();

        fixture = TestBed.createComponent(TranslationLocalesSelectorComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
