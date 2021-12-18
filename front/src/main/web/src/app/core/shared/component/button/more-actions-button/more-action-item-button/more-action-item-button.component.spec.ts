import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MoreActionItemButtonComponent } from './more-action-item-button.component';

describe('MoreActionItemButtonComponent', () => {
  let component: MoreActionItemButtonComponent;
  let fixture: ComponentFixture<MoreActionItemButtonComponent>;

  beforeEach(async(() => {
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
