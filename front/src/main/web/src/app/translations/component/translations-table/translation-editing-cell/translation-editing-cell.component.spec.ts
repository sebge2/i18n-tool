import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TranslationEditingCellComponent } from './translation-editing-cell.component';
import { FormBuilder } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CoreSharedModule } from '@i18n-core-shared';

describe('TranslationEditingCellComponent', () => {
  let component: TranslationEditingCellComponent;
  let fixture: ComponentFixture<TranslationEditingCellComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [BrowserAnimationsModule, CoreSharedModule],
      declarations: [TranslationEditingCellComponent],
    }).compileComponents();

    const formBuilder = new FormBuilder();

    fixture = TestBed.createComponent(TranslationEditingCellComponent);
    component = fixture.componentInstance;

    // const keyTranslation: BundleKeyTranslation =
    //     new BundleKeyTranslation(<BundleKeyTranslation><any>{
    //         id: 'abcd',
    //         lastEditor: '',
    //         updatedValue: null,
    //         originalValue: null,
    //         locale: Locale.FR
    //     });
    //
    // const controlsConfig = {
    //     translation: formBuilder.control(keyTranslation),
    //     value: formBuilder.control('value fr')
    // };
    // component.formGroup = formBuilder.group(controlsConfig);
    // component.formGroup.setValue(controlsConfig);
  }));

  xit('should create', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy(); // TODO issue-123
  });
});
