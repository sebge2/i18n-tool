import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RepositoryDetailsTranslationsConfigurationComponent } from './repository-details-translations-configuration.component';

describe('RepositoryDetailsTranslationsConfigurationComponent', () => {
  let component: RepositoryDetailsTranslationsConfigurationComponent;
  let fixture: ComponentFixture<RepositoryDetailsTranslationsConfigurationComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RepositoryDetailsTranslationsConfigurationComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryDetailsTranslationsConfigurationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
