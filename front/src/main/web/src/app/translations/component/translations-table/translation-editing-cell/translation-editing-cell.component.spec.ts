import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TranslationEditingCellComponent} from './translation-editing-cell.component';
import {FormBuilder} from "@angular/forms";
import {BundleKeyTranslation} from "../../../model/edition/bundle-key-translation.model";
import {Locale} from "../../../model/locale.model";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {CoreUiModule} from "../../../../core/ui/core-ui.module";

describe('TranslationEditingCellComponent', () => {
    let component: TranslationEditingCellComponent;
    let fixture: ComponentFixture<TranslationEditingCellComponent>;

    beforeEach(async(() => {
        TestBed
            .configureTestingModule({
                imports: [
                    BrowserAnimationsModule,
                    CoreUiModule
                ],
                declarations: [TranslationEditingCellComponent]
            })
            .compileComponents();

        const formBuilder = new FormBuilder();

        fixture = TestBed.createComponent(TranslationEditingCellComponent);
        component = fixture.componentInstance;

        const keyTranslation: BundleKeyTranslation =
            new BundleKeyTranslation(<BundleKeyTranslation><any>{
                id: 'abcd',
                lastEditor: '',
                updatedValue: null,
                originalValue: null,
                locale: Locale.FR
            });

        const controlsConfig = {
            translation: formBuilder.control(keyTranslation),
            value: formBuilder.control('value fr')
        };
        component.formGroup = formBuilder.group(controlsConfig);
        component.formGroup.setValue(controlsConfig);
    }));

    it('should create', () => {
        fixture.detectChanges();

        expect(component).toBeTruthy(); // TODO
    });
});
