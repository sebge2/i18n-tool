import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {CoreUiModule} from "./core/ui/core-ui.module";
import {CoreAuthModule} from "./core/auth/core-auth.module";
import {CoreEventModule} from "./core/event/core-event.module";

@NgModule({
    declarations: [
        AppComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,

        CoreUiModule,
        CoreAuthModule,
        CoreEventModule
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
