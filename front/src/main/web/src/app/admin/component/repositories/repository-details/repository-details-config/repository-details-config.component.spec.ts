import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RepositoryDetailsConfigComponent } from './repository-details-config.component';

describe('RepositoryDetailsConfigComponent', () => {
  let component: RepositoryDetailsConfigComponent;
  let fixture: ComponentFixture<RepositoryDetailsConfigComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RepositoryDetailsConfigComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryDetailsConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
