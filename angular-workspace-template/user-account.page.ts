import { Component, OnInit } from "@angular/core";

@Component({
  selector: "app-user-account",
  template: `<ion-header>
      <ion-toolbar translucent color="primary">
        <ion-buttons slot="start">
          <ion-menu-button></ion-menu-button>
        </ion-buttons>
        <ion-title>Account</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content>This is user account page</ion-content>`,
  styles: [],
})
export class UserAccountPage implements OnInit {
  constructor() {}

  ngOnInit() {}
}
