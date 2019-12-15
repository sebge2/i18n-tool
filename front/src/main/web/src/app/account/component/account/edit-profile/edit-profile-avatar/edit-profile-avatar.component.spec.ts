import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditProfileAvatarComponent } from './edit-profile-avatar.component';

describe('EditProfileAvatarComponent', () => {
  let component: EditProfileAvatarComponent;
  let fixture: ComponentFixture<EditProfileAvatarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditProfileAvatarComponent ]
    })
    .compileComponents();
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
