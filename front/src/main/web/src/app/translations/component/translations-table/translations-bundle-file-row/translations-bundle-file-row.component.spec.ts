import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationsBundleFileRowComponent } from './translations-bundle-file-row.component';

describe('TranslationsBundleFileRowComponent', () => {
  let component: TranslationsBundleFileRowComponent;
  let fixture: ComponentFixture<TranslationsBundleFileRowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TranslationsBundleFileRowComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationsBundleFileRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
