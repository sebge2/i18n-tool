import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RestoreButtonConfirmationComponent } from './restore-button-confirmation.component';

describe('RestoreButtonConfirmationComponent', () => {
  let component: RestoreButtonConfirmationComponent;
  let fixture: ComponentFixture<RestoreButtonConfirmationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RestoreButtonConfirmationComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestoreButtonConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
