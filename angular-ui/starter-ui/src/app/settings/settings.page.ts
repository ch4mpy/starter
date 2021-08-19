import { useAnimation } from '@angular/animations';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { SettingsService } from './settings.service';

@Component({
  selector: 'bar-settings',
  template: `<ion-header>
      <ion-toolbar color="primary">
        <ion-menu-button slot="start"></ion-menu-button>
        <ion-title i18n="@@settings-page_title">Paramètres</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content>
      <form [formGroup]="settingsForm">
        <ion-item-divider i18n="@@settings-page_api_section-header">URLs des APIs</ion-item-divider>
        <ion-item>
          <ion-label position="floating" i18n="@@settings-page_api_starter">foyers & contribuables</ion-label>
          <ion-input formControlName="householdsBasePath" required></ion-input>
        </ion-item>

        <div *ngIf="currentUser.isAuthenticated()">
          <ion-item-divider i18n="@@settings-page_current-user_section-header">Utilisateur</ion-item-divider>
          <ion-item>
            <ion-label i18n="@@settings-page_current-user_name">nom:</ion-label>
            <ion-label>{{ currentUser?.preferredUsername }}</ion-label>
          </ion-item>
          <ion-item>
            <ion-label>subject:</ion-label>
            <ion-label>{{ currentUser?.sub }}</ion-label>
          </ion-item>
          <ion-item>
            <ion-label>roles:</ion-label>
            <ion-label>{{ currentUser?.roles }}</ion-label>
          </ion-item>
        </div>
      </form>
    </ion-content>`,
  styles: [],
})
export class SettingsPage implements OnInit, OnDestroy {
  settingsForm = new FormGroup({
    householdsBasePath: new FormControl(null, [Validators.required]),
  });

  private settingsFormValueSubscription: Subscription;

  constructor(private settings: SettingsService, private auth: AuthService) {}

  get currentUser() {
    return this.auth.currentUser;
  }

  async ngOnInit() {
    await this.settings.getHouseholdsApiUrl().then(url => this.settingsForm.get('householdsBasePath').setValue(url));
    
    this.settingsFormValueSubscription = this.settingsForm.valueChanges.subscribe(form => {
      this.settings.setHouseholdsApiUrl(form.householdsBasePath);
    });
  }

  ngOnDestroy() {
    this.settingsFormValueSubscription.unsubscribe();
  }
}
