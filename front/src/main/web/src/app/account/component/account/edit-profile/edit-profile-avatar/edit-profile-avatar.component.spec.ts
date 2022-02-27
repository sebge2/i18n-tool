import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { EditProfileAvatarComponent } from './edit-profile-avatar.component';

describe('EditProfileAvatarComponent', () => {
  let component: EditProfileAvatarComponent;
  let fixture: ComponentFixture<EditProfileAvatarComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [EditProfileAvatarComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditProfileAvatarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
