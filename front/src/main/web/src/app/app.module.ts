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
import {ALL_LOCALES} from "./translations/model/locale.model";

export function HttpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http, './assets/i18n/', '.json');
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

        CoreUiModule,
        CoreAuthModule,
        CoreEventModule,
        CoreSharedModule
    ],
    bootstrap: [AppComponent],
    providers: [{
        provide: MESSAGE_FORMAT_CONFIG,
        useValue: {locales: ALL_LOCALES.map(locale => locale.toString())}
    }]
})
export class AppModule {
}
