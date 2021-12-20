import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { MoreActionsButtonComponent } from './more-actions-button.component';

describe('MoreActionsButtonComponent', () => {
  let component: MoreActionsButtonComponent;
  let fixture: ComponentFixture<MoreActionsButtonComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [MoreActionsButtonComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MoreActionsButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
