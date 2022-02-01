import { Component } from '@angular/core';
import { MenuController } from '@ionic/angular';

@Component({
  selector: 'app-root',
  template: `
    <ion-app>
      <ion-split-pane contentId="main">
        <ion-menu side="start" contentId="main">
          <ion-header>
            <ion-toolbar translucent>
              <ion-title>Menu</ion-title>
            </ion-toolbar>
          </ion-header>
          <ion-content>
            <ion-menu-toggle autoHide="false">
              <ion-list id="menu-items">
                <ion-item
                  routerDirection="root"
                  routerLink="/settings"
                  lines="none"
                  detail="false"
                >
                  <ion-icon slot="start" name="settings"></ion-icon>
                  <ion-label>Settings</ion-label>
                </ion-item>
                <ion-item
                  routerDirection="root"
                  routerLink="/account"
                  lines="none"
                  detail="false"
                >
                  <ion-icon slot="start" name="person-circle"></ion-icon>
                  <ion-label>Account</ion-label>
                </ion-item>
              </ion-list>
            </ion-menu-toggle>
          </ion-content>
        </ion-menu>
        <ion-router-outlet id="main"></ion-router-outlet>
      </ion-split-pane>
    </ion-app>
  `,
  styles: [],
})
export class AppComponent {
  title = 'bao-loc';

  constructor(private menuController: MenuController) {}

  public openMenu() {
    return this.menuController.open();
  }
}
