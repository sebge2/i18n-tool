import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MoreActionsButtonComponent } from './more-actions-button.component';

describe('MoreActionsButtonComponent', () => {
  let component: MoreActionsButtonComponent;
  let fixture: ComponentFixture<MoreActionsButtonComponent>;

  beforeEach(async(() => {
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
