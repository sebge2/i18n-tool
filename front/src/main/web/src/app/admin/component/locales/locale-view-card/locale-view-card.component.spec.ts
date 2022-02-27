import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { LocaleViewCardComponent } from './locale-view-card.component';

describe('LocaleViewCardComponent', () => {
  let component: LocaleViewCardComponent;
  let fixture: ComponentFixture<LocaleViewCardComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [LocaleViewCardComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LocaleViewCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
