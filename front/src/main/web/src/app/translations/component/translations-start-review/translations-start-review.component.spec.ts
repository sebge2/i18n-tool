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
    let mockDialogRef: MockDialogRef;

    class MockDialogRef {

        public closed = false;
        public data: any;

        public close(data?: any): void {
            this.closed = true;
            this.data = data;
        }

    }

    beforeEach(async(() => {
        mockDialogRef = new MockDialogRef();

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

    it('onSubmit should close the dialog', () => {
        component.form.controls.comment.setValue('my comment');

        fixture.detectChanges();

        const submitButton = fixture.debugElement.nativeElement.querySelector('#submit');

        expect(component.form.valid).toBe(true);
        expect(submitButton.disabled).toBe(false);

        submitButton.click();

        expect(mockDialogRef.closed).toBe(true);
        expect(mockDialogRef.data).not.toBeNull();
        expect(mockDialogRef.data.comment).toBe('my comment');
    });

    it('onSubmit should be disabled invalid form', () => {
        component.form.get('comment').setValue("");

        expect(fixture.debugElement.nativeElement.querySelector('#submit').disabled).toBe(true);
        expect(mockDialogRef.closed).toBe(false);
    });

    it('onCancel should close the dialog', () => {
        component.onCancel();
        expect(mockDialogRef.closed).toBe(true);
    });
});
