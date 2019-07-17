import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {CoreUiModule} from "./core/ui/core-ui.module";
import {CoreAuthModule} from "./core/auth/core-auth.module";
import {CoreEventModule} from "./core/event/core-event.module";
import {CoreSharedModule} from "./core/shared/core-shared-module";

@NgModule({
    declarations: [AppComponent],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutingModule,

        CoreUiModule,
        CoreAuthModule,
        CoreEventModule,
        CoreSharedModule
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
