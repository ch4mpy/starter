import { Component, OnDestroy, OnInit } from '@angular/core';
import { Deeplinks } from '@ionic-native/deeplinks/ngx';
import { SplashScreen } from '@ionic-native/splash-screen';
import { StatusBar } from '@ionic-native/status-bar';
import { NavController, Platform } from '@ionic/angular';
import { Subscription } from 'rxjs';
import { AuthService } from './auth/auth.service';
import { OidcUser } from './domain/oidc-user';

@Component({
  selector: 'bar-root',
  template: `<ion-app>
    <ion-split-pane contentId="main-content">
      <ion-menu contentId="main-content" type="overlay">
        <ion-header>
          <ion-toolbar translucent color="primary">
            <ion-grid>
              <ion-row>
                <ion-col size="8">
                  <ion-title>
                    {{ currentUser?.preferredUsername }}
                  </ion-title>
                </ion-col>
              </ion-row>
            </ion-grid>
          </ion-toolbar>
        </ion-header>

        <ion-content>
          <ion-menu-toggle auto-hide="false">
            <ion-item
              routerDirection="root"
              [routerLink]="['/', 'settings']"
              lines="none"
              [class.selected]="selected === 'settings'"
              class="ion-button"
            >
              <ion-icon slot="start" name="settings"></ion-icon>
              <ion-label i18n="@@side-menu_settings-label">Paramètres</ion-label>
            </ion-item>
            <ion-item
              (click)="login()"
              lines="none"
              class="ion-button"
              *ngIf="!currentUser?.isAuthenticated()"
            >
              <ion-icon slot="start" name="log-in-outline"></ion-icon>
              <ion-label i18n="@@side-menu_login-label">Identification</ion-label>
            </ion-item>
            <ion-item
              (click)="logout()"
              lines="none"
              class="ion-button"
              *ngIf="currentUser?.isAuthenticated()"
            >
              <ion-icon slot="start" name="log-out-outline"></ion-icon>
              <ion-label i18n="@@side-menu_logout-label">Déconnexion</ion-label>
            </ion-item>
          </ion-menu-toggle>
        </ion-content>
      </ion-menu>
      <ion-router-outlet id="main-content"></ion-router-outlet>
    </ion-split-pane>
  </ion-app>`,
  styles: [],
})
export class AppComponent implements OnInit, OnDestroy {
  selected = '';

  private deeplinksRouteSubscription: Subscription;
  private currentUserSubscription: Subscription;

  constructor(
    private deeplinks: Deeplinks,
    private navController: NavController,
    private platform: Platform,
    private auth: AuthService
  ) {}

  async ngOnInit() {
    console.log('PLATFORMS: ' + this.platform.platforms());
    if (this.platform.is('capacitor')) {
      this.setupDeeplinks();
      StatusBar.styleLightContent();
      SplashScreen.hide();
    }

    this.listenToUserChanges();
  }

  ngOnDestroy() {
    this.currentUserSubscription?.unsubscribe();
    this.deeplinksRouteSubscription?.unsubscribe();
  }

  login() {
    this.auth.login();
  }

  logout() {
    this.auth.logout();
  }

  get currentUser(): OidcUser {
    return this.auth.currentUser;
  }

  private setupDeeplinks() {
    this.deeplinksRouteSubscription = this.deeplinks
      .routeWithNavController(this.navController, {})
      .subscribe(
        async (match) => {
          await this.navController
            .navigateForward(match.$link.path + '?' + match.$link.queryString)
            .then(async () => this.listenToUserChanges());
        },
        (nomatch) =>
          console.error(
            "Got a deeplink that didn't match",
            JSON.stringify(nomatch)
          )
      );
  }

  private listenToUserChanges(): void {
    
    this.currentUserSubscription?.unsubscribe();
  }
}
