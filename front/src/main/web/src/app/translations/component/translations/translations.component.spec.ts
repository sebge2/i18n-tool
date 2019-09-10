import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TranslationsComponent} from './translations.component';
import {TranslationsSearchBarComponent} from "../translations-search-bar/translations-search-bar.component";
import {TranslationsTableComponent} from "../translations-table/translations-table.component";
import {TranslateModule} from "@ngx-translate/core";
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {WorkspaceSelectorComponent} from "../translations-search-bar/workspace-selector/workspace-selector.component";
import {TranslationLocalesSelectorComponent} from "../translations-search-bar/translation-locales-selector/translation-locales-selector.component";
import {TranslationCriterionSelectorComponent} from "../translations-search-bar/translation-criterion-selector/translation-criterion-selector.component";
import {TranslationEditingCellComponent} from "../translations-table/translation-editing-cell/translation-editing-cell.component";
import {HttpClientModule} from "@angular/common/http";
import {CoreEventModule} from "../../../core/event/core-event.module";
import {CoreUiModule} from "../../../core/ui/core-ui.module";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

describe('TranslationsComponent', () => {
    let component: TranslationsComponent;
    let fixture: ComponentFixture<TranslationsComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                TranslateModule.forRoot(),
                BrowserAnimationsModule,
                CoreUiModule,
                CoreSharedModule,
                CoreEventModule,
                HttpClientModule
            ],
            declarations: [
                TranslationsComponent,
                TranslationsSearchBarComponent,
                TranslationsTableComponent,
                WorkspaceSelectorComponent,
                TranslationLocalesSelectorComponent,
                TranslationCriterionSelectorComponent,
                TranslationEditingCellComponent,
            ]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(TranslationsComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
