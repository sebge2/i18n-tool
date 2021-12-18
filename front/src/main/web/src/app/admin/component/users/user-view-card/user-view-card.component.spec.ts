import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserViewCardComponent } from './user-view-card.component';

describe('UserViewCardComponent', () => {
  let component: UserViewCardComponent;
  let fixture: ComponentFixture<UserViewCardComponent>;

  beforeEach(async(() => {
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
