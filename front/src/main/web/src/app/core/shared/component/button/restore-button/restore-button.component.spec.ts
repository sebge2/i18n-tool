import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RestoreButtonComponent } from './restore-button.component';

describe('RestoreButtonComponent', () => {
  let component: RestoreButtonComponent;
  let fixture: ComponentFixture<RestoreButtonComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RestoreButtonComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestoreButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
