import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryDetailsTranslationsBundleConfigurationComponent } from './repository-details-translations-bundle-configuration.component';

describe('RepositoryDetailsTranslationsBundleConfigurationComponent', () => {
  let component: RepositoryDetailsTranslationsBundleConfigurationComponent;
  let fixture: ComponentFixture<RepositoryDetailsTranslationsBundleConfigurationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoryDetailsTranslationsBundleConfigurationComponent ]
    })
    .compileComponents();
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
