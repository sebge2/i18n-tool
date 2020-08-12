import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryDetailsBundleFileNodeComponent } from './repository-details-bundle-file-node.component';

describe('RepositoryDetailsBundleFileNodeComponent', () => {
  let component: RepositoryDetailsBundleFileNodeComponent;
  let fixture: ComponentFixture<RepositoryDetailsBundleFileNodeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoryDetailsBundleFileNodeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryDetailsBundleFileNodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
