import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { UserViewCardComponent } from './user-view-card.component';

describe('UserViewCardComponent', () => {
  let component: UserViewCardComponent;
  let fixture: ComponentFixture<UserViewCardComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [UserViewCardComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserViewCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
