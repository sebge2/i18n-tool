import {async, ComponentFixture, inject, TestBed} from '@angular/core/testing';

import {ConfirmWorkspaceDeletionComponent} from './confirm-workspace-deletion.component';
import {CoreSharedModule} from "../../../../core/shared/core-shared-module";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material";
import {BrowserDynamicTestingModule} from "@angular/platform-browser-dynamic/testing";
import {Workspace} from "../../../../translations/model/workspace.model";
import {OverlayContainer} from "@angular/cdk/overlay";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {TranslateModule} from "@ngx-translate/core";

describe('ConfirmWorkspaceDeletionComponent', () => {
    let dialog: MatDialog;
    let overlayContainer: OverlayContainer;
    let component: ConfirmWorkspaceDeletionComponent;
    let fixture: ComponentFixture<ConfirmWorkspaceDeletionComponent>;
    const mockDialogRef = {
        close: jasmine.createSpy('close')
    };

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                BrowserAnimationsModule,
                CoreSharedModule,
                TranslateModule.forRoot()
            ],
            providers: [
                {provide: MatDialogRef, useValue: mockDialogRef},
                {
                    provide: MAT_DIALOG_DATA,
                    useValue: {workspace: new Workspace(<Workspace>{branch: 'master'})}
                }
            ],
            declarations: [ConfirmWorkspaceDeletionComponent],
        });

        TestBed.overrideModule(BrowserDynamicTestingModule, {
            set: {
                entryComponents: [ConfirmWorkspaceDeletionComponent]
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
        fixture = TestBed.createComponent(ConfirmWorkspaceDeletionComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy(); // TODO
    });

    it('onCancel should close the dialog', () => {
        component.onCancel();
        expect(mockDialogRef.close).toHaveBeenCalled();
    });
});
