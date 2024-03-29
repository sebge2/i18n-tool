import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ExpansionPanelHeaderComponent } from './expansion-panel-header.component';

describe('ExpansionPanelHeaderComponent', () => {
  let component: ExpansionPanelHeaderComponent;
  let fixture: ComponentFixture<ExpansionPanelHeaderComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ExpansionPanelHeaderComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExpansionPanelHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
