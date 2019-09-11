import {async, ComponentFixture, inject, TestBed} from '@angular/core/testing';

import {TranslationsStartReviewComponent} from './translations-start-review.component';
import {TranslateModule} from "@ngx-translate/core";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material";
import {OverlayContainer} from "@angular/cdk/overlay";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {CoreUiModule} from "../../../core/ui/core-ui.module";
import {CoreSharedModule} from "../../../core/shared/core-shared-module";
import {BrowserDynamicTestingModule} from "@angular/platform-browser-dynamic/testing";

describe('TranslationsStartReviewComponent', () => {
    let dialog: MatDialog;
    let overlayContainer: OverlayContainer;
    let component: TranslationsStartReviewComponent;
    let fixture: ComponentFixture<TranslationsStartReviewComponent>;
    const mockDialogRef = {
        close: jasmine.createSpy('close')
    };

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                BrowserAnimationsModule,
                CoreUiModule,
                CoreSharedModule,
                TranslateModule.forRoot()
            ],
            providers: [
                {provide: MatDialogRef, useValue: mockDialogRef},
                {
                    provide: MAT_DIALOG_DATA,
                    useValue: {comment: null}
                }
            ],
            declarations: [TranslationsStartReviewComponent],
        });

        TestBed.overrideModule(BrowserDynamicTestingModule, {
            set: {
                entryComponents: [TranslationsStartReviewComponent]
            }
        });

        TestBed.compileComponents();
    }));

    beforeEach(inject([MatDialog, OverlayContainer],
        (d: MatDialog, oc: OverlayContainer) => {
            dialog = d;
            overlayContainer = oc;
        })
    );

    afterEach(() => {
        overlayContainer.ngOnDestroy();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(TranslationsStartReviewComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('onCancel should close the dialog', () => {
        component.onCancel();
        expect(mockDialogRef.close).toHaveBeenCalled();
    });
});
