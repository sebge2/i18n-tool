import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { MoreActionItemButtonComponent } from './more-action-item-button.component';

describe('MoreActionItemButtonComponent', () => {
  let component: MoreActionItemButtonComponent;
  let fixture: ComponentFixture<MoreActionItemButtonComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [MoreActionItemButtonComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MoreActionItemButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
