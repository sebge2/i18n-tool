import { NgModule, ModuleWithProviders, SkipSelf, Optional } from '@angular/core';
import { Configuration } from './configuration';
import { HttpClient } from '@angular/common/http';

import { AuthenticationService } from './api/authentication.service';
import { DictionaryService } from './api/dictionary.service';
import { EventService } from './api/event.service';
import { GitHubService } from './api/gitHub.service';
import { PullRequestService } from './api/pullRequest.service';
import { RepositoryService } from './api/repository.service';
import { ScheduledTaskService } from './api/scheduledTask.service';
import { SnapshotService } from './api/snapshot.service';
import { TranslationService } from './api/translation.service';
import { TranslationLocaleService } from './api/translationLocale.service';
import { UserService } from './api/user.service';
import { UserLiveSessionService } from './api/userLiveSession.service';
import { UserPreferencesService } from './api/userPreferences.service';
import { WorkspaceService } from './api/workspace.service';

@NgModule({
  imports: [],
  declarations: [],
  exports: [],
  providers: [
    AuthenticationService,
    DictionaryService,
    EventService,
    GitHubService,
    PullRequestService,
    RepositoryService,
    ScheduledTaskService,
    SnapshotService,
    TranslationService,
    TranslationLocaleService,
    UserService,
    UserLiveSessionService,
    UserPreferencesService,
    WorkspaceService,
  ],
})
export class ApiModule {
  public static forRoot(configurationFactory: () => Configuration): ModuleWithProviders {
    return {
      ngModule: ApiModule,
      providers: [{ provide: Configuration, useFactory: configurationFactory }],
    };
  }

  constructor(@Optional() @SkipSelf() parentModule: ApiModule, @Optional() http: HttpClient) {
    if (parentModule) {
      throw new Error('ApiModule is already loaded. Import in your base AppModule only.');
    }
    if (!http) {
      throw new Error(
        'You need to import the HttpClientModule in your AppModule! \n' +
          'See also https://github.com/angular/angular/issues/20575'
      );
    }
  }
}
