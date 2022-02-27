import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RepositoryDetailsBundleFileNodeComponent } from './repository-details-bundle-file-node.component';

describe('RepositoryDetailsBundleFileNodeComponent', () => {
  let component: RepositoryDetailsBundleFileNodeComponent;
  let fixture: ComponentFixture<RepositoryDetailsBundleFileNodeComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RepositoryDetailsBundleFileNodeComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryDetailsBundleFileNodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
