import {
  HttpClient,
  HttpClientModule,
} from '@angular/common/http';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';
import { Deeplinks } from '@ionic-native/deeplinks/ngx';
import { IonicModule, IonicRouteStrategy, Platform } from '@ionic/angular';
import { IonicStorageModule } from '@ionic/storage-angular';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SettingsService } from './settings/settings.service';
import { AuthConfigModule } from './auth/auth-config.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

export function init(
  platform: Platform,
  settings: SettingsService
) {
  return async () => {
    await platform.ready();
    await settings.init();
  };
}

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    IonicModule.forRoot(),
    IonicStorageModule.forRoot(),
    AuthConfigModule,
    BrowserAnimationsModule
  ],
  providers: [
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
    {
      provide: APP_INITIALIZER,
      useFactory: init,
      deps: [Platform, SettingsService],
      multi: true,
    },
    Deeplinks,
    HttpClient
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
