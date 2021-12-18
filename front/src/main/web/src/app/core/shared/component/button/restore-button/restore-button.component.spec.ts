import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RestoreButtonComponent } from './restore-button.component';

describe('RestoreButtonComponent', () => {
  let component: RestoreButtonComponent;
  let fixture: ComponentFixture<RestoreButtonComponent>;

  beforeEach(async(() => {
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
