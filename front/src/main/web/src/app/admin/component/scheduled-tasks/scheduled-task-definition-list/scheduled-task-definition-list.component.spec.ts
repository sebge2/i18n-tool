import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduledTaskDefinitionListComponent } from './scheduled-task-definition-list.component';

describe('ScheduledTaskDefinitionListComponent', () => {
  let component: ScheduledTaskDefinitionListComponent;
  let fixture: ComponentFixture<ScheduledTaskDefinitionListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ScheduledTaskDefinitionListComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScheduledTaskDefinitionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
