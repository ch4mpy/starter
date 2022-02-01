import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-settings',
  template: `<ion-header>
      <ion-toolbar translucent color="primary">
        <ion-buttons slot="start">
          <ion-menu-button></ion-menu-button>
        </ion-buttons>
        <ion-title>Settings</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content>This is Settings page</ion-content>`,
  styles: [],
})
export class SettingsPage implements OnInit {
  constructor() {}

  ngOnInit() {}
}
