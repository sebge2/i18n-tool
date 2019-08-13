import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationsStartReviewComponent } from './translations-start-review.component';

describe('TranslationsStartReviewComponent', () => {
  let component: TranslationsStartReviewComponent;
  let fixture: ComponentFixture<TranslationsStartReviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TranslationsStartReviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationsStartReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
