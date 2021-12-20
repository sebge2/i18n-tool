import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RepositoryDetailsTranslationsBundleConfigurationComponent } from './repository-details-translations-bundle-configuration.component';

describe('RepositoryDetailsTranslationsBundleConfigurationComponent', () => {
  let component: RepositoryDetailsTranslationsBundleConfigurationComponent;
  let fixture: ComponentFixture<RepositoryDetailsTranslationsBundleConfigurationComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RepositoryDetailsTranslationsBundleConfigurationComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryDetailsTranslationsBundleConfigurationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
