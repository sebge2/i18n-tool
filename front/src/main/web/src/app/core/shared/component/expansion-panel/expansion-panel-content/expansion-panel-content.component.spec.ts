import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ExpansionPanelContentComponent } from './expansion-panel-content.component';

describe('ExpansionPanelContentComponent', () => {
  let component: ExpansionPanelContentComponent;
  let fixture: ComponentFixture<ExpansionPanelContentComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ExpansionPanelContentComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExpansionPanelContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
