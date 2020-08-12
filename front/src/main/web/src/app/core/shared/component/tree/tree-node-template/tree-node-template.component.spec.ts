import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TreeNodeTemplateComponent } from './tree-node-template.component';

describe('TreeNodeTemplateComponent', () => {
  let component: TreeNodeTemplateComponent;
  let fixture: ComponentFixture<TreeNodeTemplateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TreeNodeTemplateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TreeNodeTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
