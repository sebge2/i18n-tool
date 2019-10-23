import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TranslationCriterionSelectorComponent} from './translation-criterion-selector.component';
import {CoreSharedModule} from "../../../../core/shared/core-shared-module";
import {TranslateModule} from "@ngx-translate/core";
import {CoreUiModule} from "../../../../core/ui/core-ui.module";

describe('TranslationCriterionSelectorComponent', () => {
    let component: TranslationCriterionSelectorComponent;
    let fixture: ComponentFixture<TranslationCriterionSelectorComponent>;

    beforeEach(async(() => {
        TestBed
            .configureTestingModule({
                imports: [
                    CoreUiModule,
                    CoreSharedModule,
                    TranslateModule.forRoot()
                ],
                declarations: [TranslationCriterionSelectorComponent]
            })
            .compileComponents();

        fixture = TestBed.createComponent(TranslationCriterionSelectorComponent);
        component = fixture.componentInstance;
    }));

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
