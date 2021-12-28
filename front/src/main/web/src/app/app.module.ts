import {BrowserModule, HammerModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {CoreUiModule} from '@i18n-core-ui';
import {CoreAuthModule} from '@i18n-core-auth';
import {CoreEventModule} from '@i18n-core-event';
import {TranslateCompiler, TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {HttpClient} from '@angular/common/http';
import {CoreSharedModule} from '@i18n-core-shared';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {MESSAGE_FORMAT_CONFIG, TranslateMessageFormatCompiler} from 'ngx-translate-messageformat-compiler';
import {CoreNotificationModule} from '@i18n-core-notification';
import {ApiModule, Configuration, ConfigurationParameters} from './api';
import {ALL_LOCALES} from '@i18n-core-translation';
import {CoreTranslationModule} from '@i18n-core-translation';
import {TOOL_BAR_DESCRIPTOR_PROVIDER} from '@i18n-dictionary';

export function HttpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

export function configurationFactory() {
    return new SwaggerConfiguration({
        basePath: `${window.location.protocol}//${window.location.host}`,
    });
}

export class SwaggerConfiguration extends Configuration {
    constructor(configurationParameters: ConfigurationParameters = {}) {
        super(configurationParameters);
    }
}

@NgModule({
    declarations: [AppComponent],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutingModule,

        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient],
            },
            compiler: {
                provide: TranslateCompiler,
                useClass: TranslateMessageFormatCompiler,
            },
        }),

        ApiModule.forRoot(configurationFactory),

        CoreSharedModule,
        CoreEventModule,
        CoreTranslationModule,
        CoreUiModule,
        CoreAuthModule,
        CoreNotificationModule,
        HammerModule,
    ],
    bootstrap: [AppComponent],
    providers: [
        {
            provide: MESSAGE_FORMAT_CONFIG,
            useValue: {locales: ALL_LOCALES.map((locale) => locale.toString())},
        },
        TOOL_BAR_DESCRIPTOR_PROVIDER
    ],
})
export class AppModule {
}
