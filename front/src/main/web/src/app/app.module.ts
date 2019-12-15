import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {CoreUiModule} from "./core/ui/core-ui.module";
import {CoreAuthModule} from "./core/auth/core-auth.module";
import {CoreEventModule} from "./core/event/core-event.module";
import {TranslateCompiler, TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {HttpClient} from "@angular/common/http";
import {CoreSharedModule} from "./core/shared/core-shared-module";
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {MESSAGE_FORMAT_CONFIG, TranslateMessageFormatCompiler} from "ngx-translate-messageformat-compiler";
import {CoreNotificationModule} from './core/notification/core-notification.module';
import {ApiModule, Configuration, ConfigurationParameters} from "./api";
import {ALL_LOCALES} from "./core/translation/model/tool-locale.model";
import {CoreTranslationModule} from "./core/translation/core-translation-module";

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
                deps: [HttpClient]
            },
            compiler: {
                provide: TranslateCompiler,
                useClass: TranslateMessageFormatCompiler
            }
        }),

        ApiModule.forRoot(configurationFactory),

        CoreSharedModule,
        CoreEventModule,
        CoreTranslationModule,
        CoreUiModule,
        CoreAuthModule,
        CoreNotificationModule
    ],
    bootstrap: [AppComponent],
    providers: [{
        provide: MESSAGE_FORMAT_CONFIG,
        useValue: {locales: ALL_LOCALES.map(locale => locale.toString())}
    }]
})
export class AppModule {
}
