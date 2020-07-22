import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ValidationResultNotificationComponent } from './validation-result.component';

describe('ValidationResultComponent', () => {
  let component: ValidationResultNotificationComponent;
  let fixture: ComponentFixture<ValidationResultNotificationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ValidationResultNotificationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ValidationResultNotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
