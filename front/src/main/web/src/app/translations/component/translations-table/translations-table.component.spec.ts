import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TranslationsTableComponent} from './translations-table.component';
import {TranslateModule} from "@ngx-translate/core";
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {TranslationEditingCellComponent} from "./translation-editing-cell/translation-editing-cell.component";
import {HttpClientModule} from "@angular/common/http";
import {CoreUiModule} from "../../../core/ui/core-ui.module";

describe('TranslationsTableComponent', () => {
    let component: TranslationsTableComponent;
    let fixture: ComponentFixture<TranslationsTableComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                CoreUiModule,
                CoreSharedModule,
                HttpClientModule,
                TranslateModule.forRoot()
            ],
            declarations: [TranslationsTableComponent, TranslationEditingCellComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(TranslationsTableComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
